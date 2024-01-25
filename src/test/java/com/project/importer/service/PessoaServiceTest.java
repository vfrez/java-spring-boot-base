package com.project.importer.service;

import com.project.importer.dto.response.PessoaCounterResponseDTO;
import com.project.importer.model.Pessoa;
import com.project.importer.repository.PessoaRepository;
import com.project.importer.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class PessoaServiceTest {
    @Autowired
    private PessoaService pessoaService;
    @Autowired
    private PessoaRepository pessoaRepository;

    @BeforeEach
    void beforeEach() {
        pessoaRepository.deleteAll();
    }

    @Test
    void shouldCreatePessoa() {
        Pessoa pessoa = TestUtils.createFakePessoa();

        pessoaService.savePessoa(pessoa);

        List<Pessoa> savedPessoaList = pessoaRepository.findAll();
        Assertions.assertEquals(1, savedPessoaList.size());

        Pessoa savedPessoa = savedPessoaList.get(0);

        Assertions.assertNotNull(savedPessoa.getId());
        Assertions.assertNotNull(savedPessoa.getDataCadastro());
        Assertions.assertNull(savedPessoa.getDataAtualizacao());
        Assertions.assertEquals(pessoa.getNome(), savedPessoa.getNome());
        Assertions.assertEquals(pessoa.getSobrenome(), savedPessoa.getSobrenome());
        Assertions.assertEquals(pessoa.getObservacao(), savedPessoa.getObservacao());
        Assertions.assertEquals(pessoa.getDataNascimento(), savedPessoa.getDataNascimento());

    }

    @Test
    void shouldUpdatePessoa() {
        Pessoa pessoa = TestUtils.createFakePessoa();
        Pessoa returnedPessoa = pessoaService.savePessoa(pessoa);

        Pessoa updatedPessoa = TestUtils.createFakePessoa();

        pessoaService.updatePessoaById(returnedPessoa.getId(), updatedPessoa);

        List<Pessoa> savedPessoaList = pessoaRepository.findAll();
        Assertions.assertEquals(1, savedPessoaList.size());

        Pessoa savedPessoa = savedPessoaList.get(0);

        Assertions.assertNotNull(savedPessoa.getId());
        Assertions.assertNotNull(savedPessoa.getDataCadastro());
        Assertions.assertNotNull(savedPessoa.getDataAtualizacao());
        Assertions.assertEquals(updatedPessoa.getNome(), savedPessoa.getNome());
        Assertions.assertEquals(updatedPessoa.getSobrenome(), savedPessoa.getSobrenome());
        Assertions.assertEquals(updatedPessoa.getObservacao(), savedPessoa.getObservacao());
        Assertions.assertEquals(updatedPessoa.getDataNascimento(), savedPessoa.getDataNascimento());

    }

    @Test
    void shouldGetPessoa() {
        Pessoa pessoa = TestUtils.createFakePessoa();
        Pessoa returnedSavedPessoa = pessoaService.savePessoa(pessoa);

        Pessoa returnedGetPessoa = pessoaService.getPessoaById(returnedSavedPessoa.getId()).getBody();

        List<Pessoa> savedPessoaList = pessoaRepository.findAll();
        Assertions.assertEquals(1, savedPessoaList.size());
        Pessoa pessoaFromDatabase = savedPessoaList.get(0);

        Assertions.assertEquals(pessoaFromDatabase.getId(), returnedGetPessoa.getId());
        Assertions.assertEquals(pessoaFromDatabase.getDataCadastro(), returnedGetPessoa.getDataCadastro());
        Assertions.assertEquals(pessoaFromDatabase.getDataAtualizacao(), returnedGetPessoa.getDataAtualizacao());
        Assertions.assertEquals(pessoaFromDatabase.getNome(), returnedGetPessoa.getNome());
        Assertions.assertEquals(pessoaFromDatabase.getSobrenome(), returnedGetPessoa.getSobrenome());
        Assertions.assertEquals(pessoaFromDatabase.getObservacao(), returnedGetPessoa.getObservacao());
        Assertions.assertEquals(pessoaFromDatabase.getDataNascimento(), returnedGetPessoa.getDataNascimento());

    }

    @Test
    void shouldGetAllPessoa() {
        pessoaService.savePessoa(TestUtils.createFakePessoa());
        pessoaService.savePessoa(TestUtils.createFakePessoa());
        pessoaService.savePessoa(TestUtils.createFakePessoa());
        pessoaService.savePessoa(TestUtils.createFakePessoa());

        List<Pessoa> allPessoas = pessoaService.getAllPessoas();

        Assertions.assertEquals(4, allPessoas.size());
    }

    @Test
    void shouldCountAllPessoa() {
        pessoaService.savePessoa(TestUtils.createFakePessoa());
        pessoaService.savePessoa(TestUtils.createFakePessoa());
        pessoaService.savePessoa(TestUtils.createFakePessoa());
        pessoaService.savePessoa(TestUtils.createFakePessoa());

        PessoaCounterResponseDTO pessoaCounterResponseDTO = pessoaService.countAllPessoas();

        Assertions.assertEquals(4, pessoaCounterResponseDTO.getQuantity());
    }

    @Test
    void shouldDeleteAllPessoa() {
        pessoaService.savePessoa(TestUtils.createFakePessoa());
        pessoaService.savePessoa(TestUtils.createFakePessoa());
        pessoaService.savePessoa(TestUtils.createFakePessoa());
        pessoaService.savePessoa(TestUtils.createFakePessoa());

        pessoaService.deleteAllPessoas();

        List<Pessoa> pessoaList = pessoaRepository.findAll();

        Assertions.assertEquals(0, pessoaList.size());
    }
}