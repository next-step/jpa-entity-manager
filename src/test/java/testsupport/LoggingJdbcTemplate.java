package testsupport;

import jdbc.JdbcTemplate;
import jdbc.RowMapper;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * 테스트 중에 무슨 쿼리를 실행했는지 assert 하기 위해서, execute()와 query() 를 감싸서 executedQueries 에 씁니다
 */
public class LoggingJdbcTemplate extends JdbcTemplate {
    public List<String> executedQueries;

    public LoggingJdbcTemplate(Connection connection) {
        super(connection);

        executedQueries = new ArrayList<>();
    }

    @Override
    public Long execute(String sql) {
        executedQueries.add(sql);

        return super.execute(sql);
    }

    @Override
    public <T> List<T> query(String sql, RowMapper<T> rowMapper) {
        executedQueries.add(sql);

        return super.query(sql, rowMapper);
    }
}
