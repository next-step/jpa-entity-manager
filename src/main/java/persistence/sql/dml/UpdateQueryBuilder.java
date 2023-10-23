package persistence.sql.dml;

import persistence.exception.PersistenceException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UpdateQueryBuilder {

    private final Map<String, String> data;
    private final WhereClauseBuilder whereClauseBuilder;
    private String tableName;

    private UpdateQueryBuilder() {
        this.data = new LinkedHashMap<>();
        this.whereClauseBuilder = WhereClauseBuilder.builder();
    }

    public static UpdateQueryBuilder builder() {
        return new UpdateQueryBuilder();
    }

    public UpdateQueryBuilder table(final String tableName) {
        this.tableName = tableName;
        return this;
    }

    public UpdateQueryBuilder addData(final String column, final String value) {
        this.data.put(column, value);
        return this;
    }

    public UpdateQueryBuilder addData(final List<String> columns, final List<String> values) {
        if (columns.size() != values.size()) {
            throw new PersistenceException("columns size 와 values size 가 같아야 합니다.");
        }
        for (int i = 0; i < columns.size(); i++) {
            addData(columns.get(i), values.get(i));
        }
        return this;
    }

    public UpdateQueryBuilder where(final String column, final String data) {
        this.whereClauseBuilder.and(column, data);
        return this;
    }

    public UpdateQueryBuilder where(final List<String> columns, final List<String> data) {
        if (columns.size() != data.size()) {
            throw new PersistenceException("columns size 와 data size 가 같아야 합니다.");
        }
        for (int i = 0; i < columns.size(); i++) {
            where(columns.get(i), data.get(i));
        }
        return this;
    }

    public String build() {
        if (tableName == null || tableName.isEmpty()) {
            throw new PersistenceException("테이블 이름 없이 update query 를 만들 수 없습니다.");
        }

        if (data.isEmpty()) {
            throw new PersistenceException("Data 정보 없이 update query 를 만들 수 없습니다.");
        }

        final StringBuilder builder = new StringBuilder();
        builder.append("update ")
                .append(tableName)
                .append(" set ")
                .append(buildUpdateColumnClause())
                .append(whereClauseBuilder.build());
        return builder.toString();
    }

    private String buildUpdateColumnClause() {
        return data.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(", "));
    }

}
