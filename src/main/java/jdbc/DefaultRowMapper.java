package jdbc;

import persistence.core.EntityMetaManager;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DefaultRowMapper<T> implements RowMapper<T> {

    private final Class<T> mappedClass;
    private final EntityMetaManager entityMetaManager;

    public DefaultRowMapper(Class<T> mappedClass) {
        this.mappedClass = mappedClass;
        this.entityMetaManager = EntityMetaManager.getInstance();
    }

    @Override
    public T mapRow(ResultSet resultSet) throws Exception {
        T object = mappedClass.getDeclaredConstructor().newInstance();
        int columnCount = resultSet.getMetaData().getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            String columnName = resultSet.getMetaData().getColumnName(i).toLowerCase();
            setValue(object, columnName, resultSet);
        }

        return object;
    }

    private void setValue(Object object, String columnName, ResultSet resultSet) throws SQLException {
        entityMetaManager.getEntityMetadata(object.getClass()).setValue(object, columnName, resultSet.getObject(columnName));
    }


}
