package jdbc;

import domain.EntityMetaData;
import domain.vo.ColumnName;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RowMapperImpl<T> implements RowMapper<T> {

    private final Class<T> clazz;
    private final EntityMetaData entityMetaData;

    public RowMapperImpl(Class<T> clazz, EntityMetaData entityMetaData) {
        this.clazz = clazz;
        this.entityMetaData = entityMetaData;
    }

    @Override
    public T mapRow(ResultSet resultSet) {
        T object;
        try {
            object = clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new IllegalStateException("클래스 인스턴스 생성 실패했습니다.", e);
        }

        entityMetaData.getIdAndColumnFields(object)
                .forEach(field -> {
                    field.setAccessible(true);
                    try {
                        field.set(object, resultSet.getObject(new ColumnName(entityMetaData.getFieldName(field)).getName()));
                    } catch (IllegalAccessException | SQLException e) {
                        throw new IllegalStateException("지원하는 타입이 아닙니다.");
                    }
                });

        return object;
    }
}
