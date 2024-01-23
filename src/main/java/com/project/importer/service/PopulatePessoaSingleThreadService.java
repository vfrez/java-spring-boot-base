package com.project.importer.service;

import com.github.javafaker.Faker;
import com.project.importer.dto.request.PopulateTableSingleThreadRequestDTO;
import com.project.importer.dto.response.DefaultPopulatePessoaResponse;
import com.project.importer.model.Pessoa;
import com.project.importer.repository.PessoaRepository;
import com.project.importer.utils.PessoaUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
public class PopulatePessoaSingleThreadService {

    @Autowired
    private PessoaRepository pessoaRepository;

    public DefaultPopulatePessoaResponse populatePessoaTableForLoop(PopulateTableSingleThreadRequestDTO populateTableSingleThreadRequestDTO) {
        int quantityRegisters = populateTableSingleThreadRequestDTO.getQuantity();

        log.info("Starting loop. {} Registers on a single batch. Committing each registers.", quantityRegisters);

        Faker faker = new Faker(new Locale("pt-BR"));
        StopWatch startTime = StopWatch.createStarted();

        for (int i = 0; i < quantityRegisters; i++) {
            pessoaRepository.saveAndFlush(PessoaUtils.createFakePessoa(faker));
        }

        String loadTime = startTime.formatTime();
        log.info("Generated {} registers on table PESSOA. Time to process: {}.", quantityRegisters, loadTime);

        return DefaultPopulatePessoaResponse.builder()
                .totalRegistered(quantityRegisters)
                .loadTime(loadTime)
                .build();
    }

    public DefaultPopulatePessoaResponse populatePessoaTableForLoopWithBatch(PopulateTableSingleThreadRequestDTO populateTableSingleThreadRequestDTO) {
        int quantityRegisters = populateTableSingleThreadRequestDTO.getQuantity();
        int batchSize = populateTableSingleThreadRequestDTO.getBatchSize();
        int totalBatches = quantityRegisters / batchSize;
        int actualBatch = 1;

        log.info("Starting loop. {} Registers on {} batches. Committing each batch.", quantityRegisters, totalBatches);
        StopWatch startTime = StopWatch.createStarted();
        StopWatch batchStartTime = StopWatch.createStarted();
        StopWatch fakerStartTime = StopWatch.createStarted();

        Faker faker = new Faker(new Locale("pt-BR"));
        List<Pessoa> pessoaList = new ArrayList<>();

        for (int i = 0; i < quantityRegisters; i++) {
            pessoaList.add(PessoaUtils.createFakePessoa(faker));

            if (i % batchSize == 0) {
                StopWatch repositoryStartTime = StopWatch.createStarted();
                pessoaRepository.saveAllAndFlush(pessoaList);

                log.info("Saving {} registers on database. Batch {} / {}. Duration: {}.", batchSize, actualBatch - 1, totalBatches, batchStartTime.formatTime());
                log.debug("Time to save data list: {}. Time to make a fake data: {}.", repositoryStartTime.formatTime(), fakerStartTime.formatTime());

                pessoaList.clear();
                actualBatch++;
                batchStartTime.reset();
                fakerStartTime.reset();
                batchStartTime.start();
                fakerStartTime.start();
            }
        }

        String loadTime = startTime.formatTime();

        log.info("Generated {} registers on table PESSOA. Time to process: {}.", actualBatch - 1, loadTime);

        return DefaultPopulatePessoaResponse.builder()
                .totalRegistered(actualBatch)
                .loadTime(loadTime)
                .build();
    }

}
