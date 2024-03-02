package database;

import java.util.List;

public interface Database {

    void execute(String sql);

    <T> T executeQueryForObject(Class<T> clazz, String sql);

    <T> List<T> executeQuery(Class<T> clazz, String sql);
}
