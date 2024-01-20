package task;

import com.github.javafaker.Faker;
import com.project.importer.model.Pessoa;
import com.project.importer.repository.PessoaRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.stream.IntStream;

@Slf4j
@AllArgsConstructor
public class PopulatePessoaTask implements Callable<Integer> {

    private final int quantity;
    private final int page;
    private final PessoaRepository pessoaRepository;

    @Override
    public Integer call() {
        log.info("Task for page {}, creating {} registers", page, quantity);

        List<Pessoa> pessoaList = new ArrayList<>(quantity);
        Faker faker = new Faker(new Locale("pt-BR"));

        IntStream.rangeClosed(1, quantity)
                .forEach(i -> pessoaList.add(createFakePessoa(faker)));

        pessoaRepository.saveAllAndFlush(pessoaList);
        pessoaList.clear();

        return quantity;
    }

    private Pessoa createFakePessoa(Faker faker) {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome(faker.name().firstName());
        pessoa.setSobrenome(faker.name().lastName());
        pessoa.setDataCadastro(LocalDateTime.now());
        pessoa.setObservacao(faker.lorem().sentence(10, 10));
        pessoa.setDataNascimento(faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        return pessoa;
    }
}