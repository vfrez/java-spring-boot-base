package com.project.importer.utils;

import com.github.javafaker.Faker;
import com.project.importer.model.Pessoa;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.util.Locale;

@NoArgsConstructor
public final class TestUtils {

    public static Pessoa createFakePessoa() {
        Faker faker = new Faker(new Locale("pt_BR"));

        Pessoa pessoa = new Pessoa();
        pessoa.setNome(faker.name().firstName());
        pessoa.setSobrenome(faker.name().lastName());
        pessoa.setObservacao(faker.lorem().sentence(10));
        pessoa.setDataNascimento(faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        return pessoa;
    }

}
