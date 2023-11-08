package persistence.entity;

import java.util.List;
import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import persistence.entity.persister.EntityPersister;
import persistence.sql.dml.assembler.DataManipulationLanguageAssembler;
import persistence.sql.usecase.GetFieldValueUseCase;
import persistence.sql.usecase.GetIdDatabaseFieldUseCase;

public class EntityManagerImpl implements EntityManager {
    private final RowMapper<?> rowMapper;
    private final DataManipulationLanguageAssembler dataManipulationLanguageAssembler;
    private final JdbcTemplate jdbcTemplate;
    private final EntityPersister entityPersister;
    private final GetIdDatabaseFieldUseCase getIdDatabaseFieldUseCase;
    private final GetFieldValueUseCase getFieldValueUseCase;


    public EntityManagerImpl(RowMapper<?> rowMapper, DataManipulationLanguageAssembler dataManipulationLanguageAssembler, JdbcTemplate jdbcTemplate, EntityPersister entityPersister,
                             GetIdDatabaseFieldUseCase getIdDatabaseFieldUseCase, GetFieldValueUseCase getFieldValueUseCase) {
        this.rowMapper = rowMapper;
        this.dataManipulationLanguageAssembler = dataManipulationLanguageAssembler;
        this.jdbcTemplate = jdbcTemplate;
        this.entityPersister = entityPersister;
        this.getIdDatabaseFieldUseCase = getIdDatabaseFieldUseCase;
        this.getFieldValueUseCase = getFieldValueUseCase;
    }

    @Override
    public <T> T find(Class<T> clazz, Long Id) {
        String sql = dataManipulationLanguageAssembler.generateSelectWithWhere(clazz, Id);
        List<?> query = jdbcTemplate.query(sql, rowMapper);
        if (query.size() == 0) {
            return null;
        }
        if (query.size() > 2) {
            throw new IllegalStateException("Identifier is not unique");
        }
        return (T) query.get(0);
    }

    @Override
    public Object persist(Object entity) {
        Object idValue = getFieldValueUseCase.execute(entity, getIdDatabaseFieldUseCase.execute(entity.getClass()));
        if (idValue == null) {
            entityPersister.insert(entity);
        } else {
            entityPersister.update(entity);
        }
        return entity;
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
    }
}
