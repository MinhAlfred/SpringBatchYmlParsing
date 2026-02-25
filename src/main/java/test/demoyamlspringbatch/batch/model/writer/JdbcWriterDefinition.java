package test.demoyamlspringbatch.batch.model.writer;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class JdbcWriterDefinition extends WriterDefinition {
    private String sql;
    private Map<String, Object> params; // params tĩnh nếu cần
}