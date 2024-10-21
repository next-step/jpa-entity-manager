package jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface IdMapper {
    void mapRow(final ResultSet resultSet) throws SQLException, IllegalAccessException;
}
