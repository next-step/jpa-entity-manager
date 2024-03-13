package persistence.sql;

import persistence.exception.UnsupportedClassException;

public class EntityColumnValue{

    private final Object columnValue;

    public EntityColumnValue(Object columnValue) {
        this.columnValue = columnValue;
    }

   public String queryString() {
        return queryString(columnValue);
   }

    /**
     * Get the column value from the object
     *
     * @param columnValue Column value object
     * @return Column value from the object
     */
    private static String queryString(Object columnValue) {
        // TODO: remove this else-if statement
        if (columnValue.getClass().equals(Boolean.class)) {
            return columnValue == Boolean.TRUE ? "1" : "0";
        } else if (columnValue.getClass().equals(String.class)) {
            return String.format("'%s'", columnValue);
        } else if (columnValue.getClass().equals(Integer.class)) {
            return columnValue.toString();
        } else if (columnValue.getClass().equals(Long.class)) {
            return columnValue.toString();
        }

        throw new UnsupportedClassException(columnValue.getClass());
    }
}
