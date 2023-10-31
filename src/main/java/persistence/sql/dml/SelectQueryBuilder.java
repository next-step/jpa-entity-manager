package persistence.sql.dml;

import java.util.stream.Collectors;
import persistence.dialect.Dialect;
import persistence.meta.EntityColumn;
import persistence.meta.EntityMeta;


public class SelectQueryBuilder extends DMLQueryBuilder {
    public SelectQueryBuilder(EntityMeta entityMeta, Dialect dialect) {
        super(entityMeta, dialect);
    }

    public String findAllQuery() {
        return selectQuery(getColumnsString(entityMeta)) + getFromTableQuery(entityMeta.getTableName());
    }

    public String findByIdQuery(Object id) {
        if (id == null) {
            throw new IllegalArgumentException("id가 비어 있으면 안 됩니다.");
        }

        return selectQuery(getColumnsString(entityMeta))
                + getFromTableQuery(entityMeta.getTableName())
                + whereId(getPkColumn(), id);
    }

    private String selectQuery(String fileNames) {
        return dialect.select(fileNames);
    }

    private String getColumnsString(EntityMeta entityMeta) {
        return entityMeta.getEntityColumns()
                .stream()
                .map(EntityColumn::getName)
                .collect(Collectors.joining(", "));

    }

}
