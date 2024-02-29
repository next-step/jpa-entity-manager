package persistence.sql.domain;

import static persistence.sql.CommonConstant.COLUMN_SEPARATOR;

public class Update {

    private final String updateClause;

    public Update(DatabaseTable table) {
        updateClause = table.getNormalColumn().stream()
                .map(Condition::equal)
                .map(Condition::getCondition)
                .reduce((columnA, columnB) -> String.join(COLUMN_SEPARATOR, columnA, columnB))
                .orElseThrow(() -> new IllegalStateException("fail to generate update clause"));
    }

    public String getUpdateClause() {
        return updateClause;
    }
}
