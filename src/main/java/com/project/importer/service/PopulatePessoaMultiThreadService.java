package com.project.importer.service;

import com.github.javafaker.Faker;
import com.project.importer.dto.request.PopulateTableMultiThreadRequestDTO;
import com.project.importer.model.Pessoa;
import com.project.importer.repository.PessoaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    public boolean populatePessoaTableMultiThread(PopulateTableMultiThreadRequestDTO populateTableMultiThreadRequestDTO) {
        Instant startTime = Instant.now();

        int quantity = populateTableMultiThreadRequestDTO.getQuantity();

        log.info("Starting loop. {} Registers on a single batch in parallel using default ForkPool. Committing each registers.", quantity);

        ForkJoinPool defaultPool = ForkJoinPool.commonPool();
        showPoolInfoInfoLog(defaultPool);

        Faker faker = new Faker(new Locale("pt-BR"));
        IntStream.rangeClosed(1, quantity).parallel().forEach(index -> {
            showPoolInfoDebugLog(defaultPool);
            pessoaRepository.saveAndFlush(createFakePessoa(faker));
        });

        showPoolInfoInfoLog(defaultPool);

        log.info("Generated {} registers on table PESSOA. Time to process: {}.", quantity, calculateTime(startTime, Instant.now()));

        return true;
    }

    public boolean populatePessoaTableMultiThreadNewForkJoinPool(PopulateTableMultiThreadRequestDTO populateTableMultiThreadRequestDTO) {
        Instant startTime = Instant.now();

        int quantity = populateTableMultiThreadRequestDTO.getQuantity();
        int poolSize = populateTableMultiThreadRequestDTO.getPoolSize();

        log.info("Starting loop. {} Registers on a single batch in parallel using {} thread on new ThreadPool. Committing each registers.", quantity, poolSize);

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

        log.info("Generated {} registers on table PESSOA. Time to process: {}.", registeredCounter, calculateTime(startTime, Instant.now()));

        return true;
    }

    private Callable<Integer> runParallelCreation(int quantity, ForkJoinPool customThreadPool) {
        Faker faker = new Faker(new Locale("pt-BR"));
        AtomicReference<Integer> counter = new AtomicReference<>(0);
        return () -> {
            IntStream.rangeClosed(1, quantity).parallel().forEach(i -> {
                showPoolInfoDebugLog(customThreadPool);
                pessoaRepository.saveAndFlush(createFakePessoa(faker));
                counter.getAndSet(counter.get() + 1); //Não funcionou bem, o valor deve se perder nas multi thread
            });
            return counter.get();
        };
    }

    private Duration calculateTime(Instant startTime, Instant endTime) {
        return Duration.between(startTime, endTime);
    }

    private Pessoa createFakePessoa(Faker faker) {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome(faker.name().firstName());
        pessoa.setSobrenome(faker.name().lastName());
        pessoa.setDataCadastro(LocalDateTime.now());
        pessoa.setObservacao(faker.lorem().sentence(10, 10));
        pessoa.setDataNascimento(faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        return pessoa;
    }

    private void showPoolInfoDebugLog(ForkJoinPool pool) {
        log.debug("activeThreads= {} runningThreads= {} poolSize= {} queuedTasks= {} queuedSubmissions= {} parallelism= {} stealCount= {}", pool.getActiveThreadCount(), pool.getRunningThreadCount(), pool.getPoolSize(), pool.getQueuedTaskCount(), pool.getQueuedSubmissionCount(), pool.getParallelism(), pool.getStealCount());
    }

    private void showPoolInfoInfoLog(ForkJoinPool pool) {
        log.info("activeThreads= {} runningThreads= {} poolSize= {} queuedTasks= {} queuedSubmissions= {} parallelism= {} stealCount= {}", pool.getActiveThreadCount(), pool.getRunningThreadCount(), pool.getPoolSize(), pool.getQueuedTaskCount(), pool.getQueuedSubmissionCount(), pool.getParallelism(), pool.getStealCount());
    }
}
