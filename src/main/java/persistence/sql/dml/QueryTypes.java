package persistence.sql.dml;

public final class QueryTypes {

    public static final int FIND_BY_ID = 1;
    public static final int FIND_ALL = 2;
    public static final int UPDATE = 3;
    public static final int INSERT = 4;
    public static final int DELETE = 5;
    public static final int DELETE_BY_ID = 6;
    public static final int DELETE_ALL = 7;

    private QueryTypes() {
    }

}
