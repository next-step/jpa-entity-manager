package persistence.sql.ddl;

import jakarta.persistence.Column;
import persistence.core.EntityLoader;
import persistence.entity.metadata.EntityMetadata;
import persistence.inspector.ClsssMetadataInspector;
import persistence.inspector.EntityFieldInspector;
import persistence.inspector.EntityInfoExtractor;
import persistence.sql.Dialect;
import persistence.sql.ddl.h2.H2DDLQueryGenerator;
import persistence.sql.h2.H2Dialect;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class H2DDLQueryBuilder extends H2DDLQueryGenerator implements DDLQueryBuilder {

    public H2DDLQueryBuilder() {
        this.dialect = new H2Dialect();
    }

    @Override
    public String createTableQuery(Class<?> clazz) {
        EntityMetadata entityMetadata = EntityLoader.getEntityMetadata(clazz);

        return createTableQuery(entityMetadata);
    }

    @Override
    public String dropTableQuery(Class<?> clazz) {
        EntityMetadata entityMetadata = EntityLoader.getEntityMetadata(clazz);

        return createDropQuery(entityMetadata);
    }

}
