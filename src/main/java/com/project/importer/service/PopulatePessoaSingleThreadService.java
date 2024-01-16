package com.project.importer.service;

import com.github.javafaker.Faker;
import com.project.importer.dto.PopulateTableSingleThreadRequestDTO;
import com.project.importer.model.Pessoa;
import com.project.importer.repository.PessoaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
public class PopulatePessoaSingleThreadService {

    @Autowired
    private PessoaRepository pessoaRepository;

    public boolean populatePessoaTableForLoop(PopulateTableSingleThreadRequestDTO populateTableSingleThreadRequestDTO) {
        int quantityRegisters = populateTableSingleThreadRequestDTO.getQuantity();

        log.info("Starting loop. {} Registers on a single batch. Committing each registers.", quantityRegisters);

        Faker faker = new Faker(new Locale("pt-BR"));
        Instant startTime = Instant.now();
        for (int i = 0; i < quantityRegisters; i++) {
            pessoaRepository.saveAndFlush(createFakePessoa(faker));
        }

        Instant endTime = Instant.now();
        log.info("Generated {} registers on table PESSOA. Time to process: {}.", quantityRegisters, calculateTime(startTime, endTime));

        return true;
    }

    public boolean populatePessoaTableForLoopWithBatch(PopulateTableSingleThreadRequestDTO populateTableSingleThreadRequestDTO) {
        int quantityRegisters = populateTableSingleThreadRequestDTO.getQuantity();
        int batchSize = populateTableSingleThreadRequestDTO.getBatchSize();
        int totalBatches = quantityRegisters / batchSize;
        int actualBatch = 1;

        log.info("Starting loop. {} Registers on {} batches. Committing each batch.", quantityRegisters, totalBatches);
        Instant startTime = Instant.now();
        Instant batchStartTime = Instant.now();
        Instant fakerStartTime = Instant.now();

        Faker faker = new Faker(new Locale("pt-BR"));
        List<Pessoa> pessoaList = new ArrayList<>();
        for (int i = 0; i < quantityRegisters; i++) {
            pessoaList.add(createFakePessoa(faker));

            if (i % batchSize == 0) {
                Instant repositoryStartTime = Instant.now();
                pessoaRepository.saveAllAndFlush(pessoaList);

                log.info("Saving {} registers on database. Batch {}/{}. Duration: {}.", batchSize, actualBatch, totalBatches, calculateTime(batchStartTime, Instant.now()));
                log.debug("Time to save data list: {}. Time to make a fake data: {}.", calculateTime(repositoryStartTime, Instant.now()), calculateTime(fakerStartTime, Instant.now()));

                pessoaList.clear();
                actualBatch++;
                batchStartTime = Instant.now();
                fakerStartTime = Instant.now();
            }
        }

        log.info("Generated {} registers on table PESSOA. Time to process: {}.", quantityRegisters, calculateTime(startTime, Instant.now()));

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
}
