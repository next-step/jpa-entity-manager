package persistence.sql.dml;

import persistence.sql.exception.ExceptionMessage;
import persistence.sql.exception.RequiredObjectException;
import persistence.sql.model.EntityColumnNames;
import persistence.sql.model.EntityColumnValues;
import persistence.sql.model.TableName;

public class DeleteQuery {

    private static final String SPACE = " ";

    private DeleteQuery() {
    }

    private static class DeleteQueryHolder {
        public static final DeleteQuery INSTANCE = new DeleteQuery();
    }

    public static DeleteQuery getInstance() {
        return DeleteQueryHolder.INSTANCE;
    }

    public String makeQuery(Object object) {
        if (object == null) {
            throw new RequiredObjectException(ExceptionMessage.REQUIRED_OBJECT);
        }

        if (object instanceof Class) {
            throw new IllegalArgumentException("잘못된 Object 타입입니다.");
        }

        Class<?> clazz = object.getClass();
        TableName tableName = new TableName(clazz);

        StringBuilder deleteStringBuilder = new StringBuilder();
        deleteStringBuilder.append("DELETE FROM");
        deleteStringBuilder.append(SPACE);
        deleteStringBuilder.append(tableName.getValue());
        deleteStringBuilder.append(SPACE);
        deleteStringBuilder.append("WHERE");
        deleteStringBuilder.append(SPACE);
        deleteStringBuilder.append(new EntityColumnNames(clazz).getIdColumnName().getValue());
        deleteStringBuilder.append("=");
        deleteStringBuilder.append(new EntityColumnValues(object).getField(new EntityColumnNames(clazz).getIdColumnName().getValue()));
        return deleteStringBuilder.toString();
    }
}
