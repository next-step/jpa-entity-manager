package persistence.sql.ddl;

import persistence.sql.QueryBuilder;
import persistence.sql.ddl.domain.Column;
import persistence.sql.ddl.domain.Columns;
import persistence.sql.ddl.domain.Table;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CreateQueryBuilder implements QueryBuilder {

    private static final String CREATE_TABLE_QUERY = "CREATE TABLE %s (%s);";

    private final Columns columns;
    private final Table table;

    public CreateQueryBuilder(Class<?> clazz) {
        this.columns = new Columns(clazz);
        this.table = new Table(clazz);
    }

    @Override
    public String build() {
        return String.format(
                CREATE_TABLE_QUERY,
                table.getName(),
                generateColumns()
        );
    }

    private String generateColumns() {
        return columns.getColumns().stream()
                .map(this::generateColumn)
                .collect(Collectors.joining(COMMA));
    }

    private String generateColumn(Column column) {
        return Stream.of(column.getName(),
                        generateColumnType(column),
                        generateColumnAttribute(column))
                .filter(columnPiece -> !columnPiece.isEmpty())
                .collect(Collectors.joining(SPACE));
    }

    private String generateColumnType(Column column) {
        return String.join(EMPTY_STRING,
                DIALECT.getTypeString(column.getType()),
                column.getLength());
    }

    private String generateColumnAttribute(Column defaultColumn) {
        return Stream.of(
                        DIALECT.getPrimaryKeyString(defaultColumn),
                        DIALECT.getGenerationTypeString(defaultColumn),
                        DIALECT.getConstraintString(defaultColumn))
                .filter(attributePiece -> !attributePiece.isEmpty())
                .collect(Collectors.joining(SPACE));
    }

}
