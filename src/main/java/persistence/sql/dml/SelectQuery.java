package persistence.sql.dml;

import persistence.sql.exception.ExceptionMessage;
import persistence.sql.exception.RequiredClassException;
import persistence.sql.model.EntityColumnNames;
import persistence.sql.model.TableName;

public class SelectQuery {

    private static final String SPACE = " ";
    private SelectQuery() {
    }

    private static class SelectQueryHolder {
        public static final SelectQuery INSTANCE = new SelectQuery();
    }

    public static SelectQuery getInstance() {
        return SelectQueryHolder.INSTANCE;
    }

    public String findAll(Class<?> clazz) {
        if (clazz == null) {
            throw new RequiredClassException(ExceptionMessage.REQUIRED_CLASS);
        }

        TableName tableName = new TableName(clazz);
        EntityColumnNames entityColumnNames = new EntityColumnNames(clazz);
        return String.format("SELECT %s FROM %s", entityColumnNames.getColumnNames(), tableName.getValue());
    }


    public String findById(Class<?> clazz, Object id) {
        if (clazz == null) {
            throw new RequiredClassException(ExceptionMessage.REQUIRED_CLASS);
        }

        if (id == null) {
            throw new IllegalArgumentException("id가 존재하지 않습니다.");
        }

        TableName tableName = new TableName(clazz);
        EntityColumnNames entityColumnNames = new EntityColumnNames(clazz);

        StringBuilder findByIdQueryStringBuilder = new StringBuilder();
        findByIdQueryStringBuilder.append("SELECT");
        findByIdQueryStringBuilder.append(SPACE);
        findByIdQueryStringBuilder.append(entityColumnNames.getColumnNames());
        findByIdQueryStringBuilder.append(SPACE);
        findByIdQueryStringBuilder.append("FROM");
        findByIdQueryStringBuilder.append(SPACE);
        findByIdQueryStringBuilder.append(tableName.getValue());
        findByIdQueryStringBuilder.append(SPACE);
        findByIdQueryStringBuilder.append("WHERE");
        findByIdQueryStringBuilder.append(" ");
        findByIdQueryStringBuilder.append("id=");
        findByIdQueryStringBuilder.append(id);
        return findByIdQueryStringBuilder.toString();
    }

}
