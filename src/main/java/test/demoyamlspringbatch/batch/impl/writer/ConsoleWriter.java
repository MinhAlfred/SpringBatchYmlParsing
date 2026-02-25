package test.demoyamlspringbatch.batch.impl.writer;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConsoleWriter
        implements ItemWriter<String> {


    @Override
    public void write(Chunk<? extends String> chunk) throws Exception {
        chunk.forEach(System.out::println);
    }
}