package persistence.sql.ddl;

import java.util.concurrent.ConcurrentHashMap;
import persistence.sql.dialect.Dialect;
import persistence.sql.meta.Table;

public class DdlGenerator {

    private final CreateQueryBuilder createQueryBuilder;
    private final DropQueryBuilder dropQueryBuilder;

    private DdlGenerator(Dialect dialect) {
        this.createQueryBuilder = CreateQueryBuilder.from(dialect);
        this.dropQueryBuilder = DropQueryBuilder.from();
    }

    private static class Holder {
        static final ConcurrentHashMap<Dialect, DdlGenerator> INSTANCE_MAP = new ConcurrentHashMap<>();
    }

    public static DdlGenerator getInstance(Dialect dialect) {
        return Holder.INSTANCE_MAP.computeIfAbsent(dialect, t -> new DdlGenerator(dialect));
    }

    public String generateCreateQuery(Class<?> clazz) {
        return createQueryBuilder.generateQuery(getTable(clazz));
    }

    public String generateDropQuery(Class<?> clazz) {
        return dropQueryBuilder.generateQuery(getTable(clazz));
    }

    private Table getTable(Class<?> clazz) {
        return Table.getInstance(clazz);
    }
}
