package persistence.clause;

import persistence.common.FieldValue;

import java.util.List;

public class WhereClause {

    public static String getQuery(List<FieldValue> fieldValueList) {
        StringBuilder sb = new StringBuilder(" WHERE");
        fieldValueList.forEach(fieldValue -> sb
                .append(" ")
                .append(fieldValue.getFieldName())
                .append("=")
                .append(fieldValue.getValue()));

        return sb.toString();
    }
}
