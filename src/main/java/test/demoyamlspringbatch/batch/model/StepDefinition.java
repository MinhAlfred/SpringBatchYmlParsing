package test.demoyamlspringbatch.batch.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ChunkStepDefinition.class, name = "chunk"),
        @JsonSubTypes.Type(value = TaskletStepDefinition.class, name = "tasklet")
})
public abstract class StepDefinition {
    private String name;
}
