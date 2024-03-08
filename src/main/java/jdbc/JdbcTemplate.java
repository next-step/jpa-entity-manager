package jdbc;

import java.sql.*;
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

    public Object queryAndGetGeneratedKey(final String sql) {
        try {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            int affectedRows = statement.executeUpdate();
            checkAffectedRows(affectedRows);

            ResultSet keys = statement.getGeneratedKeys();
            return getGeneratedKey(keys);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Object getGeneratedKey(ResultSet keys) throws SQLException {
        if (keys.next()) {
            return keys.getObject(1);
        }
        throw new RuntimeException("No key was generated.");
    }

    private void checkAffectedRows(int affectedRows) {
        if (affectedRows == 0) {
            throw new RuntimeException("No rows were inserted.");
        }
    }
}
