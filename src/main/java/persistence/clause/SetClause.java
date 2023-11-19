package persistence.clause;

import persistence.common.FieldValue;

import java.util.List;

public class SetClause {
    public static String getQuery(List<FieldValue> fieldValueList) {
        StringBuilder sb = new StringBuilder(" SET");
        fieldValueList.forEach(fv -> {
            String value = fv.getValue();
            if (fv.getClazz().equals(String.class)) {
                value = "'" + value + "'";
            }

            sb.append(" ")
                    .append(fv.getFieldName())
                    .append("=")
                    .append(value)
                    .append(",");
        });

        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
