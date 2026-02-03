package test.demoyamlspringbatch.batch.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class TaskletStepDefinition extends StepDefinition {

    private String tasklet;
    private Map<String, Object> params;
}