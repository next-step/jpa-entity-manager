package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dialect.Dialect;
import persistence.sql.dialect.DialectFactory;
import persistence.sql.dml.DmlQueryGenerator;
import persistence.sql.meta.ColumnMeta;
import persistence.sql.meta.ColumnMetas;
import persistence.sql.meta.EntityMeta;

import java.lang.reflect.Field;
import java.util.Arrays;

public class EntityPersister {

    private static final boolean PROCESSED_SUCCESS = true;

    private final JdbcTemplate jdbcTemplate;
    private final DmlQueryGenerator dmlQueryGenerator;

    private EntityPersister(JdbcTemplate jdbcTemplate, DmlQueryGenerator dmlQueryGenerator) {
        this.jdbcTemplate = jdbcTemplate;
        this.dmlQueryGenerator = dmlQueryGenerator;
    }

    public static EntityPersister of(JdbcTemplate jdbcTemplate) {
        DialectFactory dialectFactory = DialectFactory.getInstance();
        Dialect dialect = dialectFactory.getDialect(jdbcTemplate.getDbmsName());
        DmlQueryGenerator dmlQueryGenerator = DmlQueryGenerator.of(dialect);
        return new EntityPersister(jdbcTemplate, dmlQueryGenerator);
    }

    public void insert(Object entity) {
        String insertEntityQuery = dmlQueryGenerator.generateInsertQuery(entity);
        EntityMeta entityMeta = EntityMeta.of(entity.getClass());
        ColumnMetas columnMetas = entityMeta.getColumnMetas();
        if (columnMetas.hasAutoGenId()) {
            Long generatedKey = jdbcTemplate.executeWithGeneratedKey(insertEntityQuery);
            setGeneratedId(entity, generatedKey);
            return;
        }
        jdbcTemplate.execute(insertEntityQuery);
    }

    private void setGeneratedId(Object entity, Long generatedKey) {
        Field autoGenIdField = Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(field -> {
                    ColumnMeta columnMeta = ColumnMeta.of(field);
                    return columnMeta.isGenerationTypeIdentity();
                })
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("IDENTITY GEN Type 컬럼이 존재하지 않습니다."));

        try {
            autoGenIdField.setAccessible(true);
            autoGenIdField.set(entity, generatedKey);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Id 컬럼 값 세팅 중 오류가 발생하였습니다.");
        }
    }

    public boolean update(Object entity) {
        String updateEntityQuery = dmlQueryGenerator.generateUpdateQuery(entity);
        jdbcTemplate.execute(updateEntityQuery);
        return PROCESSED_SUCCESS;
    }

    public void delete(Object entity) {
        String deleteEntityQuery = dmlQueryGenerator.generateDeleteQuery(entity);
        jdbcTemplate.execute(deleteEntityQuery);
    }

}
