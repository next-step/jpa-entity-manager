package persistence.sql.util;

import java.util.stream.Collectors;

public final class ColumnConditions {
    private static final String DELIMITER = ", ";

    private ColumnConditions() {}

    public static String forUpsert(Object entity) {
        return ColumnFields.forUpsert(entity.getClass())
                .stream().map(
                        field -> ColumnCondition.build(field, entity)
                ).collect(Collectors.joining(DELIMITER));
    }

    public static String forId(Object entity) {
        return ColumnFields.forId(entity.getClass())
                .stream().map(
                        field -> ColumnCondition.build(field, entity)
                ).collect(Collectors.joining(DELIMITER));
    }
}
