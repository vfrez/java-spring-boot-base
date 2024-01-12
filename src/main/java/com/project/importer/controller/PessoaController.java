package com.project.importer.controller;

import com.project.importer.entity.Pessoa;
import com.project.importer.repository.PessoaRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class PessoaController {

    @Autowired
    private PessoaRepository pessoaRepository;

    @GetMapping(value = "/pessoa", produces = "application/json")
    public List<Pessoa> get() {
        return pessoaRepository.findAll();
    }

    @GetMapping(value = "/pessoa/{id}", produces = "application/json")
    public ResponseEntity<Pessoa> getById(@PathVariable(value = "id") long id) {
        Optional<Pessoa> pessoa = pessoaRepository.findById(id);

        return pessoa.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(value = "/pessoa")
    public Pessoa post(@Valid @RequestBody Pessoa pessoa) {
        return pessoaRepository.saveAndFlush(pessoa);
    }

    @PutMapping(value = "/pessoa/{id}")
    public ResponseEntity<Pessoa> put(@PathVariable(value = "id") long id, @Valid @RequestBody Pessoa newPessoa) {

        Optional<Pessoa> oldPessoaOpt = pessoaRepository.findById(id);

        if (oldPessoaOpt.isPresent()) {
            Pessoa pessoa = oldPessoaOpt.get();
            pessoa.setId(newPessoa.getId());
            pessoa.setNome(newPessoa.getNome());
            pessoaRepository.saveAndFlush(pessoa);

            return new ResponseEntity<Pessoa>(pessoa, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "/pessoa/{id}")
    public ResponseEntity<Object> delete(@PathVariable(value = "id") long id) {
        Optional<Pessoa> pessoa = pessoaRepository.findById(id);
        if (pessoa.isPresent()) {
            pessoaRepository.delete(pessoa.get());

            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}