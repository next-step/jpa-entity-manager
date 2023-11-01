package persistence.context;


import fixtures.EntityFixtures;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.DatabaseTest;
import persistence.entity.attribute.EntityAttributes;
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
                EntityAttributes entityAttributes = new EntityAttributes();
                PersistenceContext persistenceContext = new PersistenceContextImpl(simpleEntityPersister, entityAttributes);

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
                EntityAttributes entityAttributes = new EntityAttributes();
                PersistenceContext persistenceContext = new PersistenceContextImpl(simpleEntityPersister, entityAttributes);

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
            @DisplayName("엔트리 상태를 REMOVED로 변경한다.")
            void removeInstanceInFirstCache() throws SQLException {
                setUpFixtureTable(EntityFixtures.SampleOneWithValidAnnotation.class, new H2SqlConverter());

                EntityFixtures.SampleOneWithValidAnnotation sample =
                        new EntityFixtures.SampleOneWithValidAnnotation("민준", 29);

                EntityLoader entityLoader = new EntityLoaderImpl(new JdbcTemplate(server.getConnection()));
                SimpleEntityPersister simpleEntityPersister = new SimpleEntityPersister(jdbcTemplate, entityLoader);
                EntityAttributes entityAttributes = new EntityAttributes();
                PersistenceContext persistenceContext = new PersistenceContextImpl(simpleEntityPersister, entityAttributes);

                persistenceContext.addEntity(sample);

                EntityFixtures.SampleOneWithValidAnnotation retrieved
                        = persistenceContext.getEntity(EntityFixtures.SampleOneWithValidAnnotation.class, "1");

                Assertions.assertDoesNotThrow(() -> persistenceContext.removeEntity(retrieved));
            }
        }
    }

    @Nested
    @DisplayName("getDatabaseSnapshot 메소드는")
    class getDatabaseSnapshot {
        @Nested
        @DisplayName("인스턴스와 아이디가 주어지면")
        class withInstanceAndId {

            @Test
            @DisplayName("스냅샷을 반환한다.")
            void returnIfExists() throws SQLException {
                setUpFixtureTable(EntityFixtures.SampleOneWithValidAnnotation.class, new H2SqlConverter());

                EntityFixtures.SampleOneWithValidAnnotation sample =
                        new EntityFixtures.SampleOneWithValidAnnotation("민준", 29);

                EntityLoader entityLoader = new EntityLoaderImpl(new JdbcTemplate(server.getConnection()));
                SimpleEntityPersister simpleEntityPersister = new SimpleEntityPersister(jdbcTemplate, entityLoader);
                EntityAttributes entityAttributes = new EntityAttributes();
                PersistenceContext persistenceContext = new PersistenceContextImpl(simpleEntityPersister, entityAttributes);

                persistenceContext.addEntity(sample);

                assertThat(persistenceContext.getDatabaseSnapshot(sample, "1").toString())
                        .isEqualTo("SampleOneWithValidAnnotation{id=1, name='민준', age=29}");
            }
        }
    }
}
