package com.project.importer.service;

import com.github.javafaker.Faker;
import com.project.importer.dto.PopulateTableMultiThreadRequestDTO;
import com.project.importer.model.Pessoa;
import com.project.importer.repository.PessoaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@Slf4j
@Service
public class PopulatePessoaMultiThreadService {

    @Autowired
    private PessoaRepository pessoaRepository;

    public boolean populatePessoaTableMultiThread(PopulateTableMultiThreadRequestDTO populateTableMultiThreadRequestDTO) {
        ForkJoinPool pool = ForkJoinPool.commonPool();
        showPoolInfoInfoLog(pool);

        int quantity = populateTableMultiThreadRequestDTO.getQuantity();
        Faker faker = new Faker(new Locale("pt-BR"));
        Instant startTime = Instant.now();
        IntStream.rangeClosed(1, quantity).parallel().forEach(index -> {
            showPoolInfoDebugLog(pool);
            pessoaRepository.saveAndFlush(createFakePessoa(faker));
        });

        showPoolInfoInfoLog(pool);

        log.info("Generated {} registers on table PESSOA. Time to process: {}.", quantity, calculateTime(startTime, Instant.now()));

        return true;
    }

    public boolean populatePessoaTableMultiThreadNewForkJoinPool(PopulateTableMultiThreadRequestDTO populateTableMultiThreadRequestDTO) {
        Faker faker = new Faker(new Locale("pt-BR"));

        int quantity = populateTableMultiThreadRequestDTO.getQuantity();
        int poolSize = populateTableMultiThreadRequestDTO.getPoolSize();

        Instant startTime = Instant.now();

        List<Long> aList = LongStream.rangeClosed(1, quantity).boxed().collect(Collectors.toList());
        ForkJoinPool customThreadPool = new ForkJoinPool(poolSize);
        showPoolInfoInfoLog(customThreadPool);

        try {
            customThreadPool.submit(() -> {
                aList.parallelStream().forEach(i -> {
                    showPoolInfoDebugLog(customThreadPool);
                    pessoaRepository.saveAndFlush(createFakePessoa(faker));
                });
                return 1;
            }).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            showPoolInfoInfoLog(customThreadPool);
            customThreadPool.shutdown();
        }

        log.info("Generated {} registers on table PESSOA. Time to process: {}.", quantity, calculateTime(startTime, Instant.now()));

        return true;
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
