package persistence.sql.dml;

import persistence.sql.meta.Table;

public class DeleteQueryBuilder {

    private static final String DELETE_DEFINITION = "DELETE FROM %s";

    private DeleteQueryBuilder() {
    }

    private static class Holder {
        static final DeleteQueryBuilder INSTANCE = new DeleteQueryBuilder();
    }

    public static DeleteQueryBuilder getInstance() {
        return Holder.INSTANCE;
    }

    public String generateQuery(Table table) {
        return String.format(DELETE_DEFINITION, table.getTableName());
    }
}
