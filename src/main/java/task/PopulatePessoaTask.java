package task;

import com.github.javafaker.Faker;
import com.project.importer.model.Pessoa;
import com.project.importer.repository.PessoaRepository;
import com.project.importer.utils.PessoaUtils;
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
    private final int totalPages;
    private final PessoaRepository pessoaRepository;

    @Override
    public Integer call() {
        log.info("Task for page {} / {}, creating {} registers", page, totalPages, quantity);

        List<Pessoa> pessoaList = new ArrayList<>(quantity);
        Faker faker = new Faker(new Locale("pt-BR"));

        IntStream.rangeClosed(1, quantity).forEach(i -> pessoaList.add(PessoaUtils.createFakePessoa(faker)));

        pessoaRepository.saveAllAndFlush(pessoaList);
        pessoaList.clear();

        return quantity;
    }
}