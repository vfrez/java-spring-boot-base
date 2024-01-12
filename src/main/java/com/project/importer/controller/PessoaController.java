package com.project.importer.controller;

import com.project.importer.model.Pessoa;
import com.project.importer.service.PessoaService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.Objects.isNull;

@Slf4j
@RestController
public class PessoaController {

    @Autowired
    private PessoaService pessoaService;

    @GetMapping(value = "/pessoa", produces = "application/json")
    public List<Pessoa> get() {
        return pessoaService.getAllPessoas();
    }

    @GetMapping(value = "/pessoa/{id}", produces = "application/json")
    public ResponseEntity<Pessoa> getById(@PathVariable(value = "id") long id) {
        return pessoaService.getPessoaById(id);
    }

    @PostMapping(value = "/pessoa")
    public Pessoa post(@Valid @RequestBody Pessoa pessoa) {
        return pessoaService.savePessoa(pessoa);
    }

    @PutMapping(value = "/pessoa/{id}")
    public ResponseEntity<Pessoa> put(@PathVariable(value = "id") long id, @Valid @RequestBody Pessoa newPessoa) {
        Pessoa pessoa = pessoaService.updatePessoaById(id, newPessoa);

        return isNull(pessoa) ? new ResponseEntity<>(HttpStatus.NOT_FOUND) : new ResponseEntity<>(pessoa, HttpStatus.OK);

    }

    @DeleteMapping(value = "/pessoa/{id}")
    public ResponseEntity<Object> delete(@PathVariable(value = "id") long id) {
        boolean isDropped = pessoaService.deletePessoaById(id);

        return isDropped ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}