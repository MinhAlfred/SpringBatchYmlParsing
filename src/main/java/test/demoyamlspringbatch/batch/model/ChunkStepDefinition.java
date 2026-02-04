package test.demoyamlspringbatch.batch.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import test.demoyamlspringbatch.batch.model.reader.ReaderDefinition;

@Getter
@Setter
public class ChunkStepDefinition extends StepDefinition {
    private int chunkSize;

    private ReaderDefinition reader;
    private RefDefinition processor;
    private RefDefinition writer;
}