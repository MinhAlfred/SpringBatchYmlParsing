package test.demoyamlspringbatch.batch.model.reader;

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
        @JsonSubTypes.Type(value = BeanReaderDefinition.class, name = "bean"),
        @JsonSubTypes.Type(value = JdbcReaderDefinition.class, name = "jdbc")
})
public abstract class ReaderDefinition {
}