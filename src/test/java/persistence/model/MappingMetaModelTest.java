package persistence.model;

import jdbc.JdbcTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.EntityMetaDataTestSupport;
import persistence.PersonV3FixtureFactory;
import persistence.entity.persister.EntityPersister;
import persistence.entity.persister.SingleTableEntityPersister;
import persistence.sql.ddl.PersonV3;
import persistence.sql.dialect.Dialect;
import persistence.sql.dialect.H2Dialect;
import persistence.sql.dml.DefaultDmlQueryBuilder;
import persistence.sql.mapping.TableBinder;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MappingMetaModelTest extends EntityMetaDataTestSupport {

    private final Class<?> clazz = PersonV3.class;
    private final TableBinder tableBinder = new TableBinder();

    private final Dialect dialect= new H2Dialect();
    private final DefaultDmlQueryBuilder dmlQueryBuilder = new DefaultDmlQueryBuilder(dialect);
    private static JdbcTemplate jdbcTemplate;
    private final SingleTableEntityPersister entityPersister = new SingleTableEntityPersister(clazz.getName(), tableBinder, dmlQueryBuilder, jdbcTemplate, clazz);

    private final MappingMetaModel mappingMetaModel = new MappingMetaModel(entityPersister);

    @DisplayName("저장된 EntityPersistor 를 가져온다")
    @Test
    public void getEntityDescriptor() throws Exception {
        // when
        final PersonV3 person = PersonV3FixtureFactory.generatePersonV3Stub();
        final EntityPersister result = mappingMetaModel.getEntityDescriptor(person);

        // then
        assertEquals(result, entityPersister);
    }

}
