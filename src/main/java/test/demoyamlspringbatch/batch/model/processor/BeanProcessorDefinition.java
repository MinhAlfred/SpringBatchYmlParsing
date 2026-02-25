package test.demoyamlspringbatch.batch.model.processor;

import lombok.Getter;
import lombok.Setter;
import test.demoyamlspringbatch.batch.model.writer.WriterDefinition;

@Getter
@Setter
public class BeanWriterDefinition extends WriterDefinition {
    private String bean;
}