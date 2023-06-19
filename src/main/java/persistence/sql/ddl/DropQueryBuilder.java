package persistence.sql.ddl;

public abstract class DropQueryBuilder {

    public String createInsertBuild(Class<?> clazz) {
        return String.format("drop table %s", clazz.getSimpleName());
    }
}
