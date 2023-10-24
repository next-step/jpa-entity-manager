package persistence.sql.dml;

import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.TableName;
import utils.ConditionUtils;

import java.util.List;

public class SelectQuery {

    private static final String DEFAULT_SELECT_COLUMN_QUERY = "SELECT %s FROM %s";

    private final String methodName;
    private final TableName tableName;
    private final Columns columns;
    private final Object[] args;

    public SelectQuery(String methodName, Object[] args, TableName tableName, Columns columns) {
        this.methodName = methodName;
        this.tableName = tableName;
        this.columns = columns;
        this.args = args;
    }

    public static String create(String methodName, TableName tableName, Columns columns, Object... args) {
        return new SelectQuery(methodName, args, tableName, columns).combine();
    }

    public static String create(String methodName, TableName tableName, Columns columns) {
        return new SelectQuery(methodName, null, tableName, columns).combine();
    }

    public String combine() {
        String query = parseFiled();

        if (isCondition(methodName)) {
            query = String.join(" ", query, parseWhere());
        }

        return query;
    }

    private String parseFiled() {
        return String.format(DEFAULT_SELECT_COLUMN_QUERY, parseSelectFiled(), tableName.getValue());
    }

    private String parseSelectFiled() {
        return columns.getColumnsWithComma();
    }

    private String parseWhere() {
        //TODO: 메소드명을 읽고, 필드 리스트에서 이를 꺼내오는 방식으로 개선이 필요할 것 같음
        String conditionText = methodName.replace("find", "").replace("By", "");
        List<String> conditionList = ConditionUtils.getWordsFromCamelCase(conditionText);

        String condition = ConditionBuilder.getCondition(conditionList, args);
        return condition.replace(" id ", " " + setConditionField("id") + " ");
    }

    private boolean isCondition(String methodName) {
        return methodName.contains("By");
    }

    private String setConditionField(String word) {
        if (word.equals("id")) {
            word = columns.getIdName();
        }
        return word;
    }
}
