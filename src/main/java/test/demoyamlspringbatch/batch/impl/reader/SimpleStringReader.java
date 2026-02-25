package test.demoyamlspringbatch.batch.impl.reader;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

@Component
@StepScope
public class SimpleStringReader implements ItemReader<String> {

    private final Iterator<String> it =
            List.of("hello", "spring", "batch", "yaml").iterator();
    public SimpleStringReader() {
        System.out.println(">>> SimpleStringReader CREATED: " + this.hashCode());
    }

    @Override
    public String read() {
        return it.hasNext() ? it.next() : null;
    }
}