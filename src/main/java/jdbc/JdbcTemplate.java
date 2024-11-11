package jdbc;

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

    public void execute(final String sql) {
        try (final Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Object insertAndGetPrimaryKey(final String sql) {
        try (final Statement statement = connection.createStatement()) {
            int affectedRows = statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            return getPrimaryKey(statement, affectedRows);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object getPrimaryKey(Statement statement, int affectedRows) throws SQLException {
        if (affectedRows > 0) {
            return getPrimaryKey(statement);
        }
        throw new RuntimeException("No rows affected");
    }

    private Object getPrimaryKey(Statement statement) throws SQLException {
        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return generatedKeys.getObject(1);
            }
            throw new RuntimeException("No ID generated");
        }
    }

    public <T> T queryForObject(final String sql, final RowMapper<T> rowMapper) {
        final List<T> results = query(sql, rowMapper);
        if (results.size() != 1) {
            throw new RuntimeException("Expected 1 result, got " + results.size());
        }
        return results.get(0);
    }

    public <T> List<T> query(final String sql, final RowMapper<T> rowMapper) {
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
