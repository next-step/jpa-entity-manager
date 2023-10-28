package jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface IdMapper {
    void mapId(final ResultSet resultSet) throws SQLException;
}
