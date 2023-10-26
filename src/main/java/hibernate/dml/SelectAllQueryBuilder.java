package hibernate.dml;

import java.util.List;

public class SelectAllQueryBuilder {

    public static final SelectAllQueryBuilder INSTANCE = new SelectAllQueryBuilder();

    private static final String SELECT_ALL_QUERY = "select %s from %s;";

    private static final String SELECT_AlL_QUERY_COLUMN_DELIMITER = ", ";

    private SelectAllQueryBuilder() {
    }

    public String generateQuery(final String tableName, final List<String> fieldNames) {
        return String.format(SELECT_ALL_QUERY, parseColumnQueries(fieldNames), tableName);
    }

    private String parseColumnQueries(final List<String> fieldNames) {
        return String.join(SELECT_AlL_QUERY_COLUMN_DELIMITER, fieldNames);
    }
}
