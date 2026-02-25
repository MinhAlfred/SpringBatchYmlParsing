package test.demoyamlspringbatch.batch.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import test.demoyamlspringbatch.batch.model.processor.ProcessorDefinition;
import test.demoyamlspringbatch.batch.model.reader.ReaderDefinition;
import test.demoyamlspringbatch.batch.model.writer.WriterDefinition;

@Getter
@Setter
public class ChunkStepDefinition extends StepDefinition {
    private int chunkSize;

    private ReaderDefinition reader;
    private ProcessorDefinition processor;
    private WriterDefinition writer;
}