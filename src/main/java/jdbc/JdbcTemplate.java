package jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

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

    public Object executeInsert(final String sql) {
        try (final Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql, RETURN_GENERATED_KEYS);
            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();
            return generatedKeys.getObject(1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean executeUpdate(final String sql) {
        try (final Statement statement = connection.createStatement()) {
            return statement.executeUpdate(sql) > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T queryForObject(final String sql, final RowMapper<T> rowMapper) {
        try (final ResultSet resultSet = connection.prepareStatement(sql).executeQuery()) {
            resultSet.next();
            return rowMapper.mapRow(resultSet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
