package persistence.entity;

import jdbc.DefaultIdMapper;
import jdbc.JdbcTemplate;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;
import persistence.sql.meta.EntityColumn;
import persistence.sql.meta.EntityTable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DefaultEntityPersister implements EntityPersister {
    public static final String NOT_CHANGED_MESSAGE = "변경된 필드가 없습니다.";
    
    private final JdbcTemplate jdbcTemplate;
    private final InsertQueryBuilder insertQueryBuilder;
    private final UpdateQueryBuilder updateQueryBuilder;
    private final DeleteQueryBuilder deleteQueryBuilder;

    public DefaultEntityPersister(JdbcTemplate jdbcTemplate, InsertQueryBuilder insertQueryBuilder,
                                  UpdateQueryBuilder updateQueryBuilder, DeleteQueryBuilder deleteQueryBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertQueryBuilder = insertQueryBuilder;
        this.updateQueryBuilder = updateQueryBuilder;
        this.deleteQueryBuilder = deleteQueryBuilder;
    }

    @Override
    public void insert(Object entity) {
        final String sql = insertQueryBuilder.insert(entity);
        jdbcTemplate.executeAndReturnGeneratedKeys(sql, new DefaultIdMapper(entity));
    }

    @Override
    public void update(Object entity, Object snapshot) {
        final EntityTable entityTable = new EntityTable(entity);
        final EntityTable snapshotEntityTable = new EntityTable(snapshot);
        final List<EntityColumn> dirtiedEntityColumns = getDirtiedEntityColumns(entityTable, snapshotEntityTable);
        jdbcTemplate.execute(updateQueryBuilder.update(entityTable, dirtiedEntityColumns));
    }

    @Override
    public void delete(Object entity) {
        jdbcTemplate.execute(deleteQueryBuilder.delete(entity));
    }

    private List<EntityColumn> getDirtiedEntityColumns(EntityTable entityTable, EntityTable snapshotEntityTable) {
        final List<EntityColumn> dirtiedEntityColumns = IntStream.range(0, entityTable.getColumnCount())
                .filter(i -> isDirtied(entityTable.getEntityColumn(i), snapshotEntityTable.getEntityColumn(i)))
                .mapToObj(entityTable::getEntityColumn)
                .collect(Collectors.toList());

        if (dirtiedEntityColumns.isEmpty()) {
            throw new IllegalStateException(NOT_CHANGED_MESSAGE);
        }
        return dirtiedEntityColumns;
    }

    private boolean isDirtied(EntityColumn entityColumn, EntityColumn snapshotEntityColumn) {
        return !Objects.equals(entityColumn.getValue(), snapshotEntityColumn.getValue());
    }
}
