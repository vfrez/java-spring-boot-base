package com.project.importer.utils;

import com.github.javafaker.Faker;
import com.project.importer.model.Pessoa;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;

@NoArgsConstructor
public final class PessoaUtils {

    public static Pessoa createFakePessoa(Faker faker) {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome(faker.name().firstName());
        pessoa.setSobrenome(faker.name().lastName());
        pessoa.setDataCadastro(LocalDateTime.now());
        pessoa.setObservacao(faker.lorem().sentence(10, 10));
        pessoa.setDataNascimento(faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        return pessoa;
    }

}
