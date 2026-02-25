package test.demoyamlspringbatch.batch.impl.writer;

import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;
import test.demoyamlspringbatch.batch.model.entites.OutputData;

@Component
public class OutputDataConsoleWriter implements ItemWriter<OutputData> {
    @Override
    public void write(Chunk<? extends OutputData> chunk) {
        chunk.forEach(item -> System.out.println(item.name() + " - " + item.processedAt()));
    }
}