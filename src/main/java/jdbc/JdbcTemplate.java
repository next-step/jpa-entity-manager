package jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
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

    public <T> T queryForObject(final String sql, final RowMapper<T> rowMapper) {
        try (final ResultSet resultSet = connection.prepareStatement(sql).executeQuery()) {
            if (!resultSet.next()) {
                return null;
            }
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

//    public ResultSet getGeneratedKeys(final String sql) throws SQLException {
//        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//
//            int affectedRows = statement.executeUpdate();
//
//            // 자동 증가된 키 값을 가져오기 위해 ResultSet 사용
//            try (ResultSet resultSet = statement.getGeneratedKeys()) {
//                if (resultSet.next()) {
//                    // 자동 증가된 키 값 가져오기
//                    long generatedKey = resultSet.getLong(1);
//
//                    // 가져온 키 값을 사용하여 원하는 작업 수행
//            ...
//                }
//            }
//        }
//    }
}
