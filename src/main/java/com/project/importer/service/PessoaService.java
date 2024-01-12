package com.project.importer.service;


import com.project.importer.model.Pessoa;
import com.project.importer.repository.PessoaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    public List<Pessoa> getAllPessoas() {
        return pessoaRepository.findAll();
    }

    public ResponseEntity<Pessoa> getPessoaById(long id) {
        Optional<Pessoa> pessoa = pessoaRepository.findById(id);

        return pessoa.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    public Pessoa savePessoa(Pessoa pessoa) {
        return pessoaRepository.saveAndFlush(pessoa);
    }

    public Pessoa updatePessoaById(long id, Pessoa newPessoa) {
        Optional<Pessoa> oldPessoaOpt = pessoaRepository.findById(id);

        if (oldPessoaOpt.isPresent()) {
            Pessoa pessoa = oldPessoaOpt.get();
            pessoa.setId(newPessoa.getId());
            pessoa.setNome(newPessoa.getNome());

            return pessoaRepository.saveAndFlush(pessoa);
        } else {
            return null;
        }
    }

    public boolean deletePessoaById(long id) {
        Optional<Pessoa> pessoa = pessoaRepository.findById(id);

        if (pessoa.isPresent()) {
            pessoaRepository.delete(pessoa.get());
            log.info("Dropped register");

            return true;
        } else {
            return false;
        }

    }
}
