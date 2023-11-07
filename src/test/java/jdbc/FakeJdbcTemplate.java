package jdbc;

import jdbc.JdbcTemplate;
import jdbc.RowMapper;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class FakeJdbcTemplate extends JdbcTemplate {
    private String latestExecutionSqlResult;
    private Object latestQueryForObjectResult;
    private Object latestQueryResult;

    public FakeJdbcTemplate() {
        super(null);
    }

    @Override
    public void execute(String sql) {
        this.latestExecutionSqlResult = sql;
    }

    @Override
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper) {
        try {
            latestQueryForObjectResult = rowMapper.mapRow(null);

            return (T) latestQueryForObjectResult;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object executeWithGeneratedKey(String sql) {
        this.latestExecutionSqlResult = sql;

        return 0L;
    }

    @Override
    public <T> List<T> query(String sql, RowMapper<T> rowMapper) {
        try {
            latestQueryResult = rowMapper.mapRow(null);

            return (List<T>) Collections.singletonList(latestQueryResult);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getLatestExecutionSqlResult() {
        return latestExecutionSqlResult;
    }

    public Object getLatestQueryForObjectResult() {
        return latestQueryForObjectResult;
    }

    public Object getLatestQueryResult() {
        return latestQueryResult;
    }
}
