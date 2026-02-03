package test.demoyamlspringbatch.batch.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class JobDefinition {
    private String name;
    private Map<String, Object> parameters;
    private ScheduleDefinition schedule;
    private List<StepDefinition> steps;
}