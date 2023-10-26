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

public class QueryGenerator<T> {
    private final EntityMeta entityMeta;
    private final Dialect dialect;

    private QueryGenerator(EntityMeta entityMeta, Dialect dialect) {
        if (entityMeta == null) {
            throw new NoEntityException();
        }
        this.entityMeta = entityMeta;
        this.dialect = dialect;
    }

    public static <T> QueryGenerator<T> of(Class<T> tClass, Dialect dialect) {
        return new QueryGenerator<>(new EntityMeta(tClass), dialect);
    }

    public static <T> QueryGenerator<T> of(EntityMeta entityMeta, Dialect dialect) {
        return new QueryGenerator<>(entityMeta, dialect);
    }

    public String create() {
        return new CreateQueryBuilder<>(entityMeta, dialect).create();
    }

    public String drop() {
        return new DropQueryBuilder<>(entityMeta, dialect).drop();
    }

    public String insert(T object) {
        return new InsertQueryBuilder<>(entityMeta, dialect).insert(object);
    }

    public String delete(Object id) {
        return new DeleteQueryBuilder<>(entityMeta, dialect).getDeleteQuery(id);
    }

    public SelectQueryBuilder<T> select() {
        return new SelectQueryBuilder<>(entityMeta, dialect);
    }

    public String update(T entity) {
        return new UpdateQueryBuilder<>(entityMeta, dialect).update(entity);
    }


}
