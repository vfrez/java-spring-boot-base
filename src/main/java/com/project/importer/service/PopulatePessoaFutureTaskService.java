package com.project.importer.service;

import com.project.importer.dto.request.PopulateTableSingleThreadRequestDTO;
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

    public boolean populatePessoaFutureTask(PopulateTableSingleThreadRequestDTO populateTableSingleThreadRequestDTO) {
        StopWatch stopWatch = StopWatch.createStarted();

        Integer quantity = populateTableSingleThreadRequestDTO.getQuantity();
        Integer maxBatchSize = populateTableSingleThreadRequestDTO.getBatchSize();
        Integer totalPages = quantity / maxBatchSize; //Tem que arredondar pra cima
        List<PopulatePessoaTask> pessoaTaskList = new ArrayList<>(totalPages);

        IntStream.rangeClosed(1, totalPages)
                .forEach(index -> pessoaTaskList.add(new PopulatePessoaTask(maxBatchSize, index, pessoaRepository)));

        PopulatePessoaExecutor pessoaExecutor = new PopulatePessoaExecutor(populateTableSingleThreadRequestDTO.getPoolSize());

        log.info("Submitting and awaiting Tasks ...");

        pessoaExecutor.submitAndWaitWithResponse(pessoaTaskList);

        log.info("Total time for all execution {}", stopWatch.formatTime());

        return true;
    }


}
