package com.project.importer.controller;

import com.project.importer.dto.request.PopulateTableSingleThreadRequestDTO;
import com.project.importer.dto.response.DefaultPopulatePessoaResponse;
import com.project.importer.service.PopulatePessoaFutureTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("populate/pessoa")
public class PopulatePessoaFutureTaskController {

    @Autowired
    private PopulatePessoaFutureTaskService populatePessoaSingleThreadService;

    @PostMapping(value = "future-task", consumes = "application/json")
    private ResponseEntity<Object> populatePessoaFutureTask(@RequestBody PopulateTableSingleThreadRequestDTO populateTableSingleThreadRequestDTO) {
        DefaultPopulatePessoaResponse pessoaResponse = populatePessoaSingleThreadService.populatePessoaFutureTask(populateTableSingleThreadRequestDTO);
        if (Objects.nonNull(pessoaResponse)) {
            return new ResponseEntity<>(pessoaResponse, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "future-task/with-response", consumes = "application/json")
    private ResponseEntity<Object> populatePessoaFutureTaskWithResponse(@RequestBody PopulateTableSingleThreadRequestDTO populateTableSingleThreadRequestDTO) {
        DefaultPopulatePessoaResponse pessoaResponse = populatePessoaSingleThreadService.populatePessoaFutureTaskWithResponse(populateTableSingleThreadRequestDTO);
        if (Objects.nonNull(pessoaResponse)) {
            return new ResponseEntity<>(pessoaResponse, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
