package com.project.importer.controller;

import com.project.importer.dto.request.PopulateTableMultiThreadRequestDTO;
import com.project.importer.service.PopulatePessoaMultiThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("populate/pessoa")
public class PopulatePessoaMultiThreadController {

    @Autowired
    private PopulatePessoaMultiThreadService populatePessoaMultiThreadService;

    @PostMapping(value = "multiThread", consumes = "application/json")
    private ResponseEntity<Object> populatePessoaTableMultiThread(@RequestBody PopulateTableMultiThreadRequestDTO populateTableMultiThreadRequestDTO) {
        if (populatePessoaMultiThreadService.populatePessoaTableMultiThread(populateTableMultiThreadRequestDTO)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "multiThread/new-threadpool", consumes = "application/json")

    private ResponseEntity<Object> populatePessoaTableMultiThreadNewForkJoinPool(@RequestBody PopulateTableMultiThreadRequestDTO populateTableMultiThreadRequestDTO) {
        if (populatePessoaMultiThreadService.populatePessoaTableMultiThreadNewForkJoinPool(populateTableMultiThreadRequestDTO)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
