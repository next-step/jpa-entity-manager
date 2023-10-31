package persistence.sql;

import persistence.dialect.Dialect;
import persistence.exception.NoEntityException;
import persistence.meta.EntityMeta;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.ddl.DropQueryBuilder;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;

public class QueryGenerator {
    private final EntityMeta entityMeta;
    private final Dialect dialect;

    private QueryGenerator(EntityMeta entityMeta, Dialect dialect) {
        if (entityMeta == null) {
            throw new NoEntityException();
        }
        this.entityMeta = entityMeta;
        this.dialect = dialect;
    }

    public static QueryGenerator of(Class<?> tClass, Dialect dialect) {
        return of(EntityMeta.from(tClass), dialect);
    }

    public static QueryGenerator of(EntityMeta entityMeta, Dialect dialect) {
        return new QueryGenerator(entityMeta, dialect);
    }

    public String create() {
        return new CreateQueryBuilder(entityMeta, dialect).create();
    }

    public String drop() {
        return new DropQueryBuilder(entityMeta, dialect).drop();
    }

    public InsertQueryBuilder insert() {
        return new InsertQueryBuilder(entityMeta, dialect);
    }

    public DeleteQueryBuilder delete() {
        return new DeleteQueryBuilder(entityMeta, dialect);
    }

    public SelectQueryBuilder select() {
        return new SelectQueryBuilder(entityMeta, dialect);
    }

    public UpdateQueryBuilder update() {
        return new UpdateQueryBuilder(entityMeta, dialect);
    }


}
