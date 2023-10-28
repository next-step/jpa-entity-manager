package persistence.entity.persister;

import fixtures.TestEntityFixtures;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.DatabaseTest;
import persistence.entity.loader.EntityLoader;
import persistence.entity.loader.EntityLoaderImpl;
import persistence.sql.infra.H2SqlConverter;

import java.sql.SQLException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Nested
@DisplayName("EntityPersister 클래스의")
public class EntityPersisterTest extends DatabaseTest {
    TestEntityFixtures.SampleOneWithValidAnnotation sample
            = new TestEntityFixtures.SampleOneWithValidAnnotation(1, "test_nick_name", 29);

    @Nested
    @DisplayName("insert 메소드는")
    class insert {
        @Nested
        @DisplayName("적절한 인스턴스가 주어지면")
        public class withInstance {
            @Test
            @DisplayName("객체를 데이터베이스에 저장하고, 아이디가 매핑된 객체를 반환한다.")
            void returnInstanceWithIdMapping() throws SQLException {
                setUpFixtureTable(TestEntityFixtures.SampleOneWithValidAnnotation.class, new H2SqlConverter());
                JdbcTemplate jdbcTemplate = new JdbcTemplate(server.getConnection());
                EntityLoader entityLoader = new EntityLoaderImpl(jdbcTemplate);
                EntityPersister entityPersister = new EntityPersister(jdbcTemplate, entityLoader);
                TestEntityFixtures.SampleOneWithValidAnnotation inserted = entityPersister.insert(sample);
                assertThat(inserted.toString())
                        .isEqualTo("SampleOneWithValidAnnotation{id=1, name='test_nick_name', age=29}");
            }
        }
    }

    @Nested
    @DisplayName("update 메소드는")
    class update {
        @Nested
        @DisplayName("적절한 인스턴스가 주어지면")
        public class withInstance {
            @Test
            @DisplayName("객체를 데이터베이스에 저장하고, 아이디가 매핑된 객체를 반환한다.")
            void returnInstanceWithIdMapping() throws SQLException {
                setUpFixtureTable(TestEntityFixtures.SampleOneWithValidAnnotation.class, new H2SqlConverter());
                JdbcTemplate jdbcTemplate = new JdbcTemplate(server.getConnection());
                EntityLoader entityLoader = new EntityLoaderImpl(jdbcTemplate);
                EntityPersister entityPersister = new EntityPersister(jdbcTemplate, entityLoader);
                TestEntityFixtures.SampleOneWithValidAnnotation inserted = entityPersister.insert(sample);

                TestEntityFixtures.SampleOneWithValidAnnotation updatedSample
                        = new TestEntityFixtures.SampleOneWithValidAnnotation(1, "test_nick_name_updated", 29);

                TestEntityFixtures.SampleOneWithValidAnnotation updated = entityPersister.update(inserted, updatedSample);

                assertThat(updated.toString())
                        .isEqualTo("SampleOneWithValidAnnotation{id=1, name='test_nick_name_updated', age=29}");
            }
        }
    }
}
