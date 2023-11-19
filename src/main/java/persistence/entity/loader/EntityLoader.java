package persistence.entity.loader;

import java.util.Collections;
import jdbc.JdbcTemplate;
import lombok.extern.slf4j.Slf4j;
import persistence.sql.dml.assembler.DataManipulationLanguageAssembler;
import persistence.sql.usecase.GetFieldFromClass;
import persistence.sql.usecase.SetFieldValue;
import persistence.sql.vo.DatabaseField;
import persistence.sql.vo.DatabaseFields;
import persistence.sql.vo.type.BigInt;
import persistence.sql.vo.type.DatabaseType;
import persistence.sql.vo.type.Int;
import persistence.sql.vo.type.VarChar;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class EntityLoader {
    private final GetFieldFromClass getFieldFromClass;
    private final SetFieldValue setFieldValue;
    private final JdbcTemplate jdbcTemplate;
    private final DataManipulationLanguageAssembler dataManipulationLanguageAssembler;

    public EntityLoader(GetFieldFromClass getFieldFromClass, SetFieldValue setFieldValue, JdbcTemplate jdbcTemplate,
                        DataManipulationLanguageAssembler dataManipulationLanguageAssembler) {
        this.getFieldFromClass = getFieldFromClass;
        this.setFieldValue = setFieldValue;
        this.jdbcTemplate = jdbcTemplate;
        this.dataManipulationLanguageAssembler = dataManipulationLanguageAssembler;
    }

    public <T> T find(Class<T> clazz, Long id) {
        List<T> entityList = loadEntity(clazz, id);
        if (entityList.size() == 0) {
            return null;
        }
        if (entityList.size() > 2) {
            throw new IllegalStateException("Identifier is not unique");
        }
        return entityList.get(0);
    }

    private <T> List<T> loadEntity(Class<T> cls, Long id) {
        String sql = dataManipulationLanguageAssembler.generateSelectWithWhere(cls, id);
        List<T> entityList = new ArrayList<>();
        try (ResultSet resultSet = jdbcTemplate.query(sql)) {
            DatabaseFields databaseFields = getFieldFromClass.execute(cls);
            while (resultSet.next()) {
                T entity = createInstance(cls);
                entityList.add(fillEntityValue(entity, resultSet, databaseFields));
            }
            return entityList;
        } catch (Exception e) {
            log.error("Exception e", e);
        }
        return Collections.emptyList();
    }

    private <T> T createInstance(Class<T> cls) {
        try {
            return cls.getDeclaredConstructor().newInstance();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T fillEntityValue(T object, ResultSet resultSet, DatabaseFields databaseFields) {
        for (DatabaseField databaseField : databaseFields.getDatabaseFields()) {
            setFieldValue.execute(object, databaseField, getValueFromResultSet(databaseField, resultSet));
        }
        return object;
    }

    private Object getValueFromResultSet(DatabaseField databaseField, ResultSet resultSet) {
        DatabaseType databaseType = databaseField.getDatabaseType();
        try {
            if (databaseType.getClass() == BigInt.class) {
                return resultSet.getLong(databaseField.getDatabaseFieldName());
            } else if (databaseType.getClass() == Int.class) {
                return resultSet.getInt(databaseField.getDatabaseFieldName());
            } else if (databaseType.getClass() == VarChar.class) {
                return resultSet.getString(databaseField.getDatabaseFieldName());
            }
            return resultSet.getObject(databaseField.getDatabaseFieldName());
        } catch (SQLException e) {
            log.error("field set can't be done type : {}", databaseType);
            return null;
        }
    }
}
