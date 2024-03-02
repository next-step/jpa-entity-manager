package persistence.sql.dml;

public interface DmlQueryBuilder {

    String buildInsertQuery(final Insert insert);

    String buildSelectQuery(final Select select);

    String buildUpdateQuery(final Update update);

    String buildDeleteQuery(final Delete delete);

}
