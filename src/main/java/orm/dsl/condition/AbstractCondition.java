package orm.dsl.condition;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCondition implements Condition {

    protected final Object value;

    public AbstractCondition(Object value) {
        this.value = value;
    }

    // 체이닝되는 조건들을 여기에 쌓는다.
    protected final StringBuilder builder = new StringBuilder();

    // 체이닝되는 조건들은 여기에서 가지고 있는다.
    protected final List<Condition> linkedConditions = new ArrayList<>();

    // AND 조건이 체이닝됨
    @Override
    public Condition and(Condition condition) {
        linkedConditions.add(condition);
        builder.append(" AND ").append(condition);
        return this;
    }

    // ON 조건이 체이닝됨
    @Override
    public Condition or(Condition condition) {
        builder.append(" OR ").append(condition);
        return this;
    }

    protected String getAppendedConditions() {
        return builder.toString();
    }

    /**
     * SQL 상황에 맞는 데이터로 변환
     */
    protected Object getSqlAwareValue() {
        if (value instanceof String) {
            return "'" + value + "'";
        }
        return value;
    }
}