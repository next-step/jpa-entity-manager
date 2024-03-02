package persistence.sql.dml.query.clause;

import persistence.sql.entity.model.DomainTypes;

import java.util.List;
import java.util.stream.Collectors;

import static persistence.sql.constant.SqlConstant.COMMA;
import static persistence.sql.constant.SqlFormat.UPDATE_COLUMN;

public class UpdateColumnClause {

    private final List<String> column;

    public UpdateColumnClause(final List<String> column) {
        this.column = column;
    }

    public static UpdateColumnClause from(final DomainTypes domainTypes) {
        return new UpdateColumnClause(domainTypes.getDomainTypes()
                .stream()
                .filter(domainType -> !domainType.isPrimaryDomain())
                .map(domainType -> String.format(UPDATE_COLUMN.getFormat(), domainType.getColumnName(), domainType.getValue()))
                .collect(Collectors.toList()));
    }

    public String toSql() {
        return String.join(COMMA.getValue(), column);
    }

}
