package task.executor;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.lang3.time.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;


@Slf4j
public class PopulatePessoaExecutor {

    private final ExecutorService threadPool;

    public PopulatePessoaExecutor(int threadPoolSize) {
        BasicThreadFactory threadFactory = new BasicThreadFactory.Builder()
                .namingPattern("sad-thread-%d")
                .daemon(true)
                .priority(Thread.NORM_PRIORITY)
                .build();

        this.threadPool = Executors.newFixedThreadPool(threadPoolSize, threadFactory);
    }

    @SneakyThrows
    public void submitAndWaitNoResponse(List<? extends Callable<Integer>> tasks) {
        //Tutorial visto aqui https://www.callicoder.com/java-8-completablefuture-tutorial/
        //Tutorial visto aqui https://www.baeldung.com/java-completablefuture
        //Tutorial visto aqui https://stackoverflow.com/questions/19348248/waiting-on-a-list-of-future

        //Jeito 1: Não da pra ver o resultado de retorno
        StopWatch stopWatch = StopWatch.createStarted();

        List<CompletableFuture<Integer>> completableFutureList = new ArrayList<>();
        tasks.forEach(task -> {
            CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(callTask(task), threadPool);
            completableFutureList.add(completableFuture);
        });

        CompletableFuture<Void> allCompletableFutures =
                CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[completableFutureList.size()]));

        allCompletableFutures.get();
        threadPool.shutdown();

        log.info("Total time to process all tasks {}", stopWatch.formatTime());

//        List<CompletableFuture<Integer>> pageContentFutures = tasks.stream()
//                .map(task -> CompletableFuture.supplyAsync(() -> {
//                    try {
//                        return task.call();
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                }, threadPool))
//                .collect(Collectors.toList());
//
//        CompletableFuture<Integer> completableFuture3 = new CompletableFuture<>();;
//        CompletableFuture<Integer> completableFuture4 = new CompletableFuture<>();
//        List<CompletableFuture<Integer>> futureList = List.of(completableFuture3, completableFuture4);
//        CompletableFuture<Integer> completableFuture2 = CompletableFuture.allOf(futureList.toArray(new CompletableFuture[futureList.size()]))
//
//
//        completableFuture2.get();
//
//
//
//
//
//
//
//
//
//        List<Future<Integer>> futureList = new ArrayList<>();
//        tasks.forEach(task -> futureList.add(threadPool.submit(task)));
//
//        Integer registeredValues = 0;
//        try {
//
//            boolean allProcessed = false;
//
//            while (allProcessed == false) {
//
//
//                futureList.forEach(future -> {
//                    boolean done = future.isDone();
//                });
//                log.info("Tasks is not completed yet....");
//                Thread.sleep(1000); //sleep for 1 millisecond before checking again
//            }
//
//            log.info("Task is completed, let's check result");
//
//            registeredValues = future.get();
//        } catch (InterruptedException | ExecutionException e) {
//            throw new RuntimeException(e);
//        }
//        log.info("Registered value is : " + registeredValues);
//
//        threadPool.shutdown();
//
//    }

    }

    @SneakyThrows
    public Integer submitAndWaitWithResponse(List<? extends Callable<Integer>> tasks) {
        //Tutorial visto aqui https://www.callicoder.com/java-8-completablefuture-tutorial/

        //Jeito 2: Após allCompletableFutures, é feito um processo para pegar o resultado das futureTasks
        StopWatch stopWatch = StopWatch.createStarted();

        List<CompletableFuture<Integer>> completableFutureList = new ArrayList<>();
        tasks.forEach(task -> {
            CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(callTask(task), threadPool);
            completableFutureList.add(completableFuture);
        });

        CompletableFuture<Void> allCompletableFutures =
                CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[completableFutureList.size()]));

        CompletableFuture<List<Integer>> futureContents =
                allCompletableFutures.thenApply(v -> completableFutureList.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList()));

        CompletableFuture<Integer> responseOfCreatedItens =
                futureContents.thenApply(pageContents -> pageContents.stream()
                .mapToInt(content -> content)
                .sum());

        Integer generated = responseOfCreatedItens.get();
        threadPool.shutdown();

        log.info("Total time to process all {} registers {}", generated, stopWatch.formatTime());

        return generated;

    }

    private Supplier<Integer> callTask(Callable<Integer> task) {
        return () -> {
            try {
                return task.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
