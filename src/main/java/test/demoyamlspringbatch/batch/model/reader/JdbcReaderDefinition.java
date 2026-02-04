package test.demoyamlspringbatch.batch.model.reader;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class JdbcReaderDefinition extends ReaderDefinition {
    private String sql;
    private String rowMapper;
    private Map<String, Object> params;
}