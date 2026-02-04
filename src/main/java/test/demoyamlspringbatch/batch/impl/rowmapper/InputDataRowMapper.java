package test.demoyamlspringbatch.batch.impl.rowmapper;

import org.apache.catalina.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import test.demoyamlspringbatch.batch.model.entites.InputData;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component("inputDataRowMapper")
public class InputDataRowMapper implements RowMapper<InputData> {

    @Override
    public InputData mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new InputData(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("status")
        );
    }
}