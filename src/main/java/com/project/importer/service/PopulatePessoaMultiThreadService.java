package com.project.importer.service;

import com.github.javafaker.Faker;
import com.project.importer.dto.request.PopulateTableMultiThreadRequestDTO;
import com.project.importer.dto.response.DefaultPopulatePessoaResponse;
import com.project.importer.repository.PessoaRepository;
import com.project.importer.utils.PessoaUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

@Slf4j
@Service
public class PopulatePessoaMultiThreadService {

    @Autowired
    private PessoaRepository pessoaRepository;

    public DefaultPopulatePessoaResponse populatePessoaTableMultiThread(PopulateTableMultiThreadRequestDTO populateTableMultiThreadRequestDTO) {
        int quantity = populateTableMultiThreadRequestDTO.getQuantity();

        log.info("Starting loop. {} Registers on a single batch in parallel using default ForkPool. Committing each registers.", quantity);

        StopWatch startTime = StopWatch.createStarted();

        ForkJoinPool defaultPool = ForkJoinPool.commonPool();
        showPoolInfoInfoLog(defaultPool);

        Faker faker = new Faker(new Locale("pt-BR"));
        IntStream.rangeClosed(1, quantity).parallel().forEach(index -> {
            showPoolInfoDebugLog(defaultPool);
            pessoaRepository.saveAndFlush(PessoaUtils.createFakePessoa(faker));
        });

        showPoolInfoInfoLog(defaultPool);

        String loadTime = startTime.formatTime();
        log.info("Generated {} registers on table PESSOA. Time to process: {}.", quantity, loadTime);

        return DefaultPopulatePessoaResponse.builder()
                .totalRegistered(quantity)
                .loadTime(loadTime)
                .build();
    }

    public DefaultPopulatePessoaResponse populatePessoaTableMultiThreadNewForkJoinPool(PopulateTableMultiThreadRequestDTO populateTableMultiThreadRequestDTO) {
        int quantity = populateTableMultiThreadRequestDTO.getQuantity();
        int poolSize = populateTableMultiThreadRequestDTO.getPoolSize();

        log.info("Starting loop. {} Registers on a single batch in parallel using {} thread on new ThreadPool. Committing each registers.", quantity, poolSize);

        StopWatch startTime = StopWatch.createStarted();
        ForkJoinPool customThreadPool = new ForkJoinPool(poolSize);
        showPoolInfoInfoLog(customThreadPool);

        Integer registeredCounter;
        try {
            registeredCounter = customThreadPool.submit(runParallelCreation(quantity, customThreadPool)).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            showPoolInfoInfoLog(customThreadPool);
            customThreadPool.shutdown();
        }

        String loadTime = startTime.formatTime();
        log.info("Generated {} registers on table PESSOA. Time to process: {}.", registeredCounter, loadTime);

        return DefaultPopulatePessoaResponse.builder()
                .totalRegistered(registeredCounter)
                .loadTime(loadTime)
                .build();
    }

    private Callable<Integer> runParallelCreation(int quantity, ForkJoinPool customThreadPool) {
        Faker faker = new Faker(new Locale("pt-BR"));
        AtomicReference<Integer> counter = new AtomicReference<>(0);

        return () -> {
            IntStream.rangeClosed(1, quantity).parallel().forEach(i -> {
                showPoolInfoDebugLog(customThreadPool);
                pessoaRepository.saveAndFlush(PessoaUtils.createFakePessoa(faker));

                counter.getAndSet(counter.get() + 1); //Não funcionou bem, o valor deve se perder nas multi thread
            });
            return counter.get();
        };
    }

    private void showPoolInfoDebugLog(ForkJoinPool pool) {
        log.debug("activeThreads= {} runningThreads= {} poolSize= {} queuedTasks= {} queuedSubmissions= {} parallelism= {} stealCount= {}", pool.getActiveThreadCount(), pool.getRunningThreadCount(), pool.getPoolSize(), pool.getQueuedTaskCount(), pool.getQueuedSubmissionCount(), pool.getParallelism(), pool.getStealCount());
    }

    private void showPoolInfoInfoLog(ForkJoinPool pool) {
        log.info("activeThreads= {} runningThreads= {} poolSize= {} queuedTasks= {} queuedSubmissions= {} parallelism= {} stealCount= {}", pool.getActiveThreadCount(), pool.getRunningThreadCount(), pool.getPoolSize(), pool.getQueuedTaskCount(), pool.getQueuedSubmissionCount(), pool.getParallelism(), pool.getStealCount());
    }
}
