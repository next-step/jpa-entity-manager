package persistence.sql.dml.conditional;

import persistence.sql.entity.model.PrimaryDomainType;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static persistence.sql.constant.SqlConstant.*;

public class Criteria {

    private final List<Criterion> criterion;

    public Criteria(final List<Criterion> criterion) {
        this.criterion = criterion;
    }

    public static Criteria fromPkCriterion(PrimaryDomainType primaryDomainType) {
        Criterion criterion = Criterion.of(primaryDomainType.getColumnName(), primaryDomainType.getValue());
        return new Criteria(Collections.singletonList(criterion));
    }

    public static Criteria emptyInstance() {
        return new Criteria(Collections.emptyList());
    }

    public String toSql() {
        if (criterion.isEmpty()) {
            return EMPTY.getValue();
        }

        return criterion.stream()
                .map(Criterion::toSql)
                .collect(Collectors.joining(AND.getValue() + BLANK.getValue()));
    }
}
