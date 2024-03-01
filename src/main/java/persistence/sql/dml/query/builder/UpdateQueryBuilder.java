package persistence.sql.dml.query.builder;

import persistence.sql.dml.conditional.Criteria;
import persistence.sql.dml.query.clause.UpdateColumnClause;
import persistence.sql.dml.query.clause.WhereClause;
import persistence.sql.entity.EntityMappingTable;

public class UpdateQueryBuilder {

    private static final String FORMAT = "UPDATE %s SET %s WHERE %s";

    private final String tableName;
    private final WhereClause whereClause;
    private final UpdateColumnClause updateColumnClause;


    private UpdateQueryBuilder(final String tableName,
                               final WhereClause whereClause,
                               final UpdateColumnClause updateColumnClause) {
        this.tableName = tableName;
        this.whereClause = whereClause;
        this.updateColumnClause = updateColumnClause;
    }

    public static UpdateQueryBuilder of(final EntityMappingTable entityMappingTable,
                                        final Criteria criteria) {
        return new UpdateQueryBuilder(
                entityMappingTable.getTableName(),
                new WhereClause(criteria),
                UpdateColumnClause.from(entityMappingTable.getDomainTypes())
        );
    }

    public String toSql() {
        return String.format(FORMAT,
                tableName,
                updateColumnClause.toSql(),
                whereClause.toSql());
    }
}
