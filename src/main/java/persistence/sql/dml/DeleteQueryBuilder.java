package persistence.sql.dml;

public class DeleteQueryBuilder extends DmlQueryBuilder {

    public DeleteQueryBuilder(Class<?> entityClass) {
        super(entityClass);
    }

    public String buildDeleteAllQuery() {
        return deleteClause(entityClass);
    }

    private String deleteClause(Class<?> entityClass) {
        return String.format("DELETE FROM %s", getTableName(entityClass));
    }

    public String buildDeleteByIdQuery(Long id) {
        return new StringBuilder(buildDeleteAllQuery())
            .append(whereClause(id))
            .toString();
    }
}
