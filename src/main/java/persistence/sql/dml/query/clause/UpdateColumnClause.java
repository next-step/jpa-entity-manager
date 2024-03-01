package persistence.sql.dml.query.clause;

import persistence.sql.entity.model.DomainTypes;

import java.util.List;
import java.util.stream.Collectors;

public class UpdateColumnClause {

    private static final String FORMAT = "%s=%s";
    private static final String DELIMITER = ",";

    private final List<String> column;

    public UpdateColumnClause(final List<String> column) {
        this.column = column;
    }

    public static UpdateColumnClause from(final DomainTypes domainTypes) {
        return new UpdateColumnClause(domainTypes.getDomainTypes()
                .stream()
                .map(domainType -> String.format(FORMAT, domainType.getColumnName(), domainType.getValue()))
                .collect(Collectors.toList()));
    }

    public String toSql() {
        return String.join(DELIMITER, column);
    }

}
