package com.project.importer.service;

import com.github.javafaker.Faker;
import com.project.importer.dto.PopulateTableRequestDTO;
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
public class PopulateDatabaseService {

    @Autowired
    private PessoaRepository pessoaRepository;

    public boolean populatePessoaTableForLoop(PopulateTableRequestDTO populateTableRequestDTO) {
        int quantityRegisters = populateTableRequestDTO.getQuantity();
        Faker faker = new Faker(new Locale("pt-BR"));

        log.info("Starting loop. {} Registers on a single batch. Committing each registers.", quantityRegisters);

        Instant startTime = Instant.now();
        for (int i = 0; i < quantityRegisters; i++) {
            Pessoa pessoa = new Pessoa();

            pessoa.setNome(faker.name().firstName());
            pessoa.setSobrenome(faker.name().lastName());
            pessoa.setDataCadastro(LocalDateTime.now());
            pessoa.setObservacao(faker.lorem().characters(0, 100));
            pessoa.setDataNascimento(faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

            pessoaRepository.saveAndFlush(pessoa);
        }

        Instant endTime = Instant.now();
        log.info("Generated {} registers on table PESSOA. Time to process: {}.", quantityRegisters, calculateTime(startTime, endTime));

        return true;
    }

    public boolean populatePessoaTableForLoopWithBatch(PopulateTableRequestDTO populateTableRequestDTO) {
        int quantityRegisters = populateTableRequestDTO.getQuantity();
        int batchSize = populateTableRequestDTO.getBatchSize();
        int totalBatches = quantityRegisters / batchSize;
        int actualBatch = 1;

        log.info("Starting loop. {} Registers on {} batches. Committing each batch.", quantityRegisters, totalBatches);
        Instant startTime = Instant.now();
        Instant batchStartTime = Instant.now();
        Instant fakerStartTime = Instant.now();

        Faker faker = new Faker(new Locale("pt-BR"));
        List<Pessoa> pessoaList = new ArrayList<>();
        for (int i = 0; i < quantityRegisters; i++) {
            Pessoa pessoa = new Pessoa();

            pessoa.setNome(faker.name().firstName());
            pessoa.setSobrenome(faker.name().lastName());
            pessoa.setDataCadastro(LocalDateTime.now());
            pessoa.setObservacao(faker.lorem().sentence(10, 10));
            pessoa.setDataNascimento(faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

            pessoaList.add(pessoa);

            if (i % batchSize == 0) {
                log.info("Saving {} registers on database. Batch {}/{}. Duration: {}.", batchSize, actualBatch, totalBatches, calculateTime(batchStartTime, Instant.now()));

                Instant repositoryStartTime = Instant.now();
                pessoaRepository.saveAllAndFlush(pessoaList);
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
}
