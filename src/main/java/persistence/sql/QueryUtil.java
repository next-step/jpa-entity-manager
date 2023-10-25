package persistence.sql;

import persistence.sql.ddl.CreateQuery;
import persistence.sql.ddl.DropQuery;
import persistence.sql.dml.DeleteQuery;
import persistence.sql.dml.InsertQuery;
import persistence.sql.dml.SelectQuery;
import persistence.sql.dml.UpdateQuery;

public final class QueryUtil {

    private static final CreateQuery createQuery = CreateQuery.create();
    private static final DropQuery dropQuery = DropQuery.create();

    private static final SelectQuery selectQuery = SelectQuery.create();
    private static final InsertQuery insertQuery = InsertQuery.create();
    private static final UpdateQuery updateQuery = UpdateQuery.create();
    private static final DeleteQuery deleteQuery = DeleteQuery.create();

    public static CreateQuery create() {
        return createQuery;
    }

    public static DropQuery drop() {
        return dropQuery;
    }

    public static SelectQuery select() {
        return selectQuery;
    }

    public static InsertQuery insert() {
        return insertQuery;
    }

    public static UpdateQuery update() {
        return updateQuery;
    }

    public static DeleteQuery delete() {
        return deleteQuery;
    }
}
