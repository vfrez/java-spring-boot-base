package com.project.importer.service;

import com.project.importer.dto.request.PopulateTableSingleThreadRequestDTO;
import com.project.importer.dto.response.DefaultPopulatePessoaResponse;
import com.project.importer.repository.PessoaRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import task.PopulatePessoaTask;
import task.executor.PopulatePessoaExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Service
public class PopulatePessoaFutureTaskService {

    @Autowired
    private PessoaRepository pessoaRepository;

    public DefaultPopulatePessoaResponse populatePessoaFutureTask(PopulateTableSingleThreadRequestDTO populateTableSingleThreadRequestDTO) {
        StopWatch stopWatch = StopWatch.createStarted();

        Integer quantity = populateTableSingleThreadRequestDTO.getQuantity(); //Quantidade não está sendo levado para a task, assim só se cria multiplos de maxBatchSize
        Integer maxBatchSize = populateTableSingleThreadRequestDTO.getBatchSize();
        Integer totalPages = (int) Math.ceil((double) quantity / maxBatchSize);
        List<PopulatePessoaTask> pessoaTaskList = new ArrayList<>(totalPages);

        IntStream.rangeClosed(1, totalPages)
                .forEach(index -> pessoaTaskList.add(new PopulatePessoaTask(maxBatchSize, index, totalPages, pessoaRepository)));

        PopulatePessoaExecutor pessoaExecutor = new PopulatePessoaExecutor(populateTableSingleThreadRequestDTO.getPoolSize());

        log.info("Submitting and awaiting Tasks ...");

        pessoaExecutor.submitAndWaitNoResponse(pessoaTaskList);

        String loadTime = stopWatch.formatTime();
        log.info("Total time for all execution {}", loadTime);

        return DefaultPopulatePessoaResponse.builder()
                .loadTime(loadTime)
                .build();
    }

    public DefaultPopulatePessoaResponse populatePessoaFutureTaskWithResponse(PopulateTableSingleThreadRequestDTO populateTableSingleThreadRequestDTO) {
        StopWatch stopWatch = StopWatch.createStarted();

        Integer quantity = populateTableSingleThreadRequestDTO.getQuantity(); //Quantidade não está sendo levado para a task, assim só se cria multiplos de maxBatchSize
        Integer maxBatchSize = populateTableSingleThreadRequestDTO.getBatchSize();
        Integer totalPages = (int) Math.ceil((double) quantity / maxBatchSize); //Tem que arredondar pra cima
        List<PopulatePessoaTask> pessoaTaskList = new ArrayList<>(totalPages);

        IntStream.rangeClosed(1, totalPages)
                .forEach(page -> pessoaTaskList.add(new PopulatePessoaTask(maxBatchSize, page, totalPages, pessoaRepository)));

        PopulatePessoaExecutor pessoaExecutor = new PopulatePessoaExecutor(populateTableSingleThreadRequestDTO.getPoolSize());

        log.info("Submitting and awaiting Tasks ...");

        Integer totalRegistered = pessoaExecutor.submitAndWaitWithResponse(pessoaTaskList);

        String loadTime = stopWatch.formatTime();
        log.info("Total time for all execution {}", loadTime);

        return DefaultPopulatePessoaResponse.builder()
                .totalRegistered(totalRegistered)
                .loadTime(loadTime)
                .build();
    }

}
