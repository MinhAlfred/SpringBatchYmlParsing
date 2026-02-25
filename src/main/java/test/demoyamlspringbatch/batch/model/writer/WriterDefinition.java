package test.demoyamlspringbatch.batch.model.writer;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BeanWriterDefinition.class, name = "bean"),
        @JsonSubTypes.Type(value = JdbcWriterDefinition.class, name = "jdbc")
})
public abstract class WriterDefinition {
}