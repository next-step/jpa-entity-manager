package persistence.sql.ddl;

public abstract class DropQueryBuilder {

    public String createQueryBuild(Class<?> clazz) {
        return String.format("drop table %s", clazz.getSimpleName());
    }
}
