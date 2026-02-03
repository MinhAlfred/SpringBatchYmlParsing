package test.demoyamlspringbatch.batch.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChunkStepDefinition extends StepDefinition {
    private int chunkSize;

    private RefDefinition reader;
    private RefDefinition processor;
    private RefDefinition writer;
}