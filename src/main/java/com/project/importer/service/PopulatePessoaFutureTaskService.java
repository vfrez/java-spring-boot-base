package com.project.importer.service;

import com.github.javafaker.Faker;
import com.project.importer.dto.request.PopulateTableSingleThreadRequestDTO;
import com.project.importer.model.Pessoa;
import com.project.importer.repository.PessoaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Service
public class PopulatePessoaFutureTaskService {

    @Autowired
    private PessoaRepository pessoaRepository;

    public boolean populatePessoaFutureTask(PopulateTableSingleThreadRequestDTO populateTableSingleThreadRequestDTO) {

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

//    @SneakyThrows
//    public static void main(String[] args) {
//        Instant startTime = Instant.now();
//        Thread.sleep(1000);
//
//        System.out.println(Duration.between(startTime, Instant.now()).toMillis());
//
//
//        StopWatch watch = StopWatch.createStarted();
//        Thread.sleep(1000);
//
//        System.out.println(watch.getTime(TimeUnit.MILLISECONDS));
//
//    }

}
