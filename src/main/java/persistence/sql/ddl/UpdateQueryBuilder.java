package persistence.sql.ddl;

import java.util.Map;

public abstract class UpdateQueryBuilder {
    protected UpdateQueryBuilder() {

    }

    public String createUpdateBuild(Object id, String tableName, ColumnMap updateColumns) {
        return String.format("UPDATE %s SET %s WHERE %s", tableName, toUpdateString(updateColumns), "id=" + id);
    }

    private String toUpdateString(ColumnMap columnMap) {
        StringBuilder updateString = new StringBuilder();
        for (Map.Entry<String, String> entry : columnMap.entrySet()) {
            String column = entry.getKey();
            String value = entry.getValue();

            if (value.equals("null")) {
                continue;
            }

            if (!isNumeric(value)) {
                value = "'" + value + "'";
            }

            updateString.append(column).append(" = ").append(value).append(", ");
        }

        if (updateString.length() > 2) {
            updateString.setLength(updateString.length() - 2);
        }

        return updateString.toString();
    }

    private boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }
}
