package com.project.importer.service;


import com.project.importer.dto.PessoaCounterResponse;
import com.project.importer.model.Pessoa;
import com.project.importer.repository.PessoaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    public List<Pessoa> getAllPessoas() {
        return pessoaRepository.findAll();
    }

    public PessoaCounterResponse countAllPessoas() {
        long count = pessoaRepository.count();
        return new PessoaCounterResponse(count);
    }


    public boolean deleteAllPessoas() {
        pessoaRepository.deleteAllInBatch();
        return true;
    }

    public ResponseEntity<Pessoa> getPessoaById(UUID id) {
        Optional<Pessoa> pessoa = pessoaRepository.findById(id);

        return pessoa.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    public Pessoa savePessoa(Pessoa pessoa) {
        pessoa.setDataCadastro(LocalDateTime.now());
        return pessoaRepository.saveAndFlush(pessoa);
    }

    public Pessoa updatePessoaById(UUID id, Pessoa newPessoa) {
        Optional<Pessoa> oldPessoaOpt = pessoaRepository.findById(id);

        if (oldPessoaOpt.isPresent()) {
            Pessoa pessoa = oldPessoaOpt.get();
            pessoa.setDataAtualizacao(LocalDateTime.now());
            pessoa.setNome(newPessoa.getNome());

            return pessoaRepository.saveAndFlush(pessoa);
        } else {
            return null;
        }
    }

    public boolean deletePessoaById(UUID id) {
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
