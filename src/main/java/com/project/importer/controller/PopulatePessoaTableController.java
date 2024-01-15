package com.project.importer.controller;

import com.project.importer.dto.PopulateTableRequestDTO;
import com.project.importer.service.PopulateDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("populate/pessoa")
public class PopulatePessoaTableController {

    @Autowired
    private PopulateDatabaseService populateDatabaseService;

    @PostMapping(value = "for-loop", consumes = "application/json")
    private ResponseEntity<Object> populatePessoaTableForLoop(@RequestBody PopulateTableRequestDTO populateTableRequestDTO) {
        if (populateDatabaseService.populatePessoaTableForLoop(populateTableRequestDTO)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "for-loop/with-batch", consumes = "application/json")
    private ResponseEntity<Object> populatePessoaTableForLoopWithBatch(@RequestBody PopulateTableRequestDTO populateTableRequestDTO) {
        if (populateDatabaseService.populatePessoaTableForLoopWithBatch(populateTableRequestDTO)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "multiThread", consumes = "application/json")
    private ResponseEntity<Object> populatePessoaTableMultiThread(@RequestBody PopulateTableRequestDTO populateTableRequestDTO) {
        if (populateDatabaseService.populatePessoaTableMultiThread(populateTableRequestDTO)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "multiThread/new-threadpool", consumes = "application/json")
    private ResponseEntity<Object> populatePessoaTableMultiThreadNewForkJoinPool(@RequestBody PopulateTableRequestDTO populateTableRequestDTO) {
        if (populateDatabaseService.populatePessoaTableMultiThreadNewForkJoinPool(populateTableRequestDTO)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
