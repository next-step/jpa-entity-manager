package persistence.sql.dml;

public interface DmlQueryBuilder {

    String toStatementWithId(Object id);
}
