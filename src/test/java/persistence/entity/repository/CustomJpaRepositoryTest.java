package persistence.entity.repository;

import fixtures.EntityFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.DatabaseTest;
import persistence.context.PersistenceContext;
import persistence.context.PersistenceContextImpl;
import persistence.entity.attribute.EntityAttributes;
import persistence.entity.loader.EntityLoader;
import persistence.entity.loader.EntityLoaderImpl;
import persistence.entity.manager.EntityManagerImpl;
import persistence.entity.persister.SimpleEntityPersister;
import persistence.sql.infra.H2SqlConverter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Nested
@DisplayName("CustomJpaRepository 클래스의")
class CustomJpaRepositoryTest extends DatabaseTest {
    @Nested
    @DisplayName("save 메소드는")
    class save {
        @Nested
        @DisplayName("적절한 인스턴스가 주어지면")
        public class withInstance {
            @Test
            void save() {
                EntityFixtures.SampleOneWithValidAnnotation sample =
                        new EntityFixtures.SampleOneWithValidAnnotation("민준", 29);

                setUpFixtureTable(EntityFixtures.SampleOneWithValidAnnotation.class, new H2SqlConverter());

                EntityLoader entityLoader = new EntityLoaderImpl(jdbcTemplate);
                SimpleEntityPersister simpleEntityPersister = new SimpleEntityPersister(jdbcTemplate, entityLoader);
                EntityAttributes entityAttributes = new EntityAttributes();
                PersistenceContext persistenceContext = new PersistenceContextImpl(simpleEntityPersister, entityAttributes);
                EntityManagerImpl entityManager = EntityManagerImpl.of(persistenceContext);

                CustomJpaRepository customJpaRepository = new CustomJpaRepository(entityManager);

                assertThat(customJpaRepository.save(sample).toString()).isEqualTo("SampleOneWithValidAnnotation{id=1, name='민준', age=29}");
            }
        }
    }
}
