package jdbc;

import persistence.entity.exception.UpdatedEntityNotExistsException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JdbcTemplate {
    private final Connection connection;

    public JdbcTemplate(final Connection connection) {
        this.connection = connection;
    }

    public Long executeUpdate(final String sql) {
        try (final Statement statement = connection.createStatement()) {
            int resultSize = statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            if (resultSize == 0) {
                throw new IllegalStateException();
            }
            return getId(statement);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Long getId(Statement statement) throws SQLException {
        try (ResultSet resultSet = statement.getGeneratedKeys()) {
            return resultSet.next() ? resultSet.getLong(1) : null;
        }
    }

    public void execute(final String sql) {
        try (final Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T queryForObject(final String sql,
                                final RowMapper<T> rowMapper) {
        final List<T> results = query(sql, rowMapper);
        if (results.size() != 1) {
            throw new RuntimeException("Expected 1 result, got " + results.size());
        }
        return results.get(0);
    }

    public <T> List<T> query(final String sql,
                             final RowMapper<T> rowMapper) {
        try (final ResultSet resultSet = connection.prepareStatement(sql).executeQuery()) {
            final List<T> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(rowMapper.mapRow(resultSet));
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
