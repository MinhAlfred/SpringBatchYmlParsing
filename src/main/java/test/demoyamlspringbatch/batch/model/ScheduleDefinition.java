package test.demoyamlspringbatch.batch.model;

import lombok.Data;

@Data
public class ScheduleDefinition {
    private String type;
    private String expression;
}