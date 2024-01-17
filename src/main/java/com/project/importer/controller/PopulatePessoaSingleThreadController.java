package com.project.importer.controller;

import com.project.importer.dto.request.PopulateTableSingleThreadRequestDTO;
import com.project.importer.service.PopulatePessoaSingleThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("populate/pessoa")
public class PopulatePessoaSingleThreadController {

    @Autowired
    private PopulatePessoaSingleThreadService populatePessoaSingleThreadService;

    @PostMapping(value = "for-loop", consumes = "application/json")
    private ResponseEntity<Object> populatePessoaTableForLoop(@RequestBody PopulateTableSingleThreadRequestDTO populateTableSingleThreadRequestDTO) {
        if (populatePessoaSingleThreadService.populatePessoaTableForLoop(populateTableSingleThreadRequestDTO)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "for-loop/with-batch", consumes = "application/json")
    private ResponseEntity<Object> populatePessoaTableForLoopWithBatch(@RequestBody PopulateTableSingleThreadRequestDTO populateTableSingleThreadRequestDTO) {
        if (populatePessoaSingleThreadService.populatePessoaTableForLoopWithBatch(populateTableSingleThreadRequestDTO)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
