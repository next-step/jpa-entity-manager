package persistence.entity;

import jdbc.RowMapper;
import persistence.sql.dialect.H2Dialect;

public class H2GeneratedIdObtainStrategy implements GeneratedIdObtainStrategy {
    private final H2Dialect h2Dialect = new H2Dialect();

    @Override
    public String getQueryString() {
        return h2Dialect.getGeneratedIdSelectQuery();
    }

    @Override
    public RowMapper<Object> getRowMapper() {
        return rs -> rs.getObject("IDENTITY()");
    }
}
