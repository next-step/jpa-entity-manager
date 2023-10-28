package persistence.entity;

import persistence.core.EntityMetadata;
import persistence.core.EntityMetadataProvider;
import persistence.exception.PersistenceException;
import persistence.util.ReflectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EntityIdMapper {
    private final String idColumnName;
    private final String idColumnFieldName;

    public EntityIdMapper(final Class<?> clazz) {
        final EntityMetadata<?> entityMetadata = EntityMetadataProvider.getInstance().getEntityMetadata(clazz);
        idColumnName = entityMetadata.getIdColumnName();
        idColumnFieldName = entityMetadata.getIdColumnFieldName();
    }

    public void mapId(final Object entity, final ResultSet resultSet) {
        try {
            final Object idValue = resultSet.getObject(idColumnName);
            ReflectionUtils.injectField(entity, idColumnFieldName, idValue);
        } catch (final SQLException e) {
            throw new PersistenceException("ResultSet Id Mapping 중 에러가 발생했습니다.", e);
        }
    }
}
