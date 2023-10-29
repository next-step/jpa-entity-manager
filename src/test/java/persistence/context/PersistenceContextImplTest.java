package persistence.context;


import fixtures.EntityFixtures;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.DatabaseTest;
import persistence.entity.attribute.EntityAttributeCenter;
import persistence.entity.loader.EntityLoader;
import persistence.entity.loader.EntityLoaderImpl;
import persistence.entity.persister.SimpleEntityPersister;
import persistence.sql.infra.H2SqlConverter;

import java.sql.SQLException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Nested
@DisplayName("PersistenceContextImpl 클래스의")
class PersistenceContextImplTest extends DatabaseTest {
    @Nested
    @DisplayName("getEntity 메소드는")
    class getEntity {
        @Nested
        @DisplayName("클래스타입과 아이디가 주어지면")
        class withValidArgs {
            @Test
            @DisplayName("일차 캐시에 객체가 존재하면 객체를 반환하고, 없으면 null을 반환한다.")
            void returnFromFirstCache() throws SQLException {
                setUpFixtureTable(EntityFixtures.SampleOneWithValidAnnotation.class, new H2SqlConverter());

                EntityFixtures.SampleOneWithValidAnnotation sample =
                        new EntityFixtures.SampleOneWithValidAnnotation("민준", 29);

                EntityLoader entityLoader = new EntityLoaderImpl(new JdbcTemplate(server.getConnection()));
                SimpleEntityPersister simpleEntityPersister = new SimpleEntityPersister(jdbcTemplate, entityLoader);
                EntityAttributeCenter entityAttributeCenter = new EntityAttributeCenter();
                PersistenceContext persistenceContext = new PersistenceContextImpl(simpleEntityPersister, entityAttributeCenter);

                EntityFixtures.SampleOneWithValidAnnotation instance
                        = persistenceContext.getEntity(EntityFixtures.SampleOneWithValidAnnotation.class, "1");
                assertThat(instance).isEqualTo(null);

                persistenceContext.addEntity(sample);

                EntityFixtures.SampleOneWithValidAnnotation retrieved
                        = persistenceContext.getEntity(EntityFixtures.SampleOneWithValidAnnotation.class, "1");

                assertThat(retrieved.toString()).isEqualTo("SampleOneWithValidAnnotation{id=1, name='민준', age=29}");
            }
        }
    }

    @Nested
    @DisplayName("addEntity 메소드는")
    class addEntity {
        @Nested
        @DisplayName("클래스타입과 필드가 주어지면")
        class withValidArgs {

            @Test
            @DisplayName("영속성 컨택스트에 객체를 넣는다")
            void putInstanceToFirstCache() throws SQLException {
                setUpFixtureTable(EntityFixtures.SampleOneWithValidAnnotation.class, new H2SqlConverter());

                EntityFixtures.SampleOneWithValidAnnotation sample =
                        new EntityFixtures.SampleOneWithValidAnnotation("민준", 29);

                EntityLoader entityLoader = new EntityLoaderImpl(new JdbcTemplate(server.getConnection()));
                SimpleEntityPersister simpleEntityPersister = new SimpleEntityPersister(jdbcTemplate, entityLoader);
                EntityAttributeCenter entityAttributeCenter = new EntityAttributeCenter();
                PersistenceContext persistenceContext = new PersistenceContextImpl(simpleEntityPersister, entityAttributeCenter);

                persistenceContext.addEntity(sample);

                EntityFixtures.SampleOneWithValidAnnotation retrieved
                        = persistenceContext.getEntity(EntityFixtures.SampleOneWithValidAnnotation.class, "1");

                assertThat(retrieved.toString()).isEqualTo("SampleOneWithValidAnnotation{id=1, name='민준', age=29}");
            }
        }
    }

    @Nested
    @DisplayName("removeEntity 메소드는")
    class removeEntity {
        @Nested
        @DisplayName("인스턴스와 필드가 주어지면")
        class withValidArgs {

            @Test
            @DisplayName("인스턴스를 일차 캐시에서 제거한다.")
            void removeInstanceInFirstCache() throws SQLException {
                setUpFixtureTable(EntityFixtures.SampleOneWithValidAnnotation.class, new H2SqlConverter());

                EntityFixtures.SampleOneWithValidAnnotation sample =
                        new EntityFixtures.SampleOneWithValidAnnotation("민준", 29);

                EntityLoader entityLoader = new EntityLoaderImpl(new JdbcTemplate(server.getConnection()));
                SimpleEntityPersister simpleEntityPersister = new SimpleEntityPersister(jdbcTemplate, entityLoader);
                EntityAttributeCenter entityAttributeCenter = new EntityAttributeCenter();
                PersistenceContext persistenceContext = new PersistenceContextImpl(simpleEntityPersister, entityAttributeCenter);

                persistenceContext.addEntity(sample);

                EntityFixtures.SampleOneWithValidAnnotation retrieved
                        = persistenceContext.getEntity(EntityFixtures.SampleOneWithValidAnnotation.class, "1");

                persistenceContext.removeEntity(retrieved);

                assertThat(persistenceContext.getEntity(EntityFixtures.SampleOneWithValidAnnotation.class, "1"))
                        .isEqualTo(null);
            }
        }
    }
}
