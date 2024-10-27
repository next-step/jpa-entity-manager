package persistence.sql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.TestUtils;
import persistence.config.TestPersistenceConfig;
import persistence.sql.dml.Database;
import persistence.sql.fixture.TestPerson;
import persistence.sql.loader.EntityLoader;

import java.sql.SQLException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("EntityLoaderFactory 테스트")
class EntityLoaderFactoryTest {
    private Database database;

    @BeforeEach
    void setUp() throws SQLException, NoSuchFieldException, IllegalAccessException {
        TestPersistenceConfig config = TestPersistenceConfig.getInstance();
        database = config.database();

        // Spring Test Context 프레임워크의 ReflectionTestUtils 대용으로 직접 필드에 접근하여 초기화
        Map<Class<?>, EntityLoader<?>> context
                = TestUtils.getValueByFieldName(EntityLoaderFactory.getInstance(), "context");
        context.clear();
    }

    @Test
    @DisplayName("EntityLoaderFactory 는 싱글톤이다.")
    void testSingleton() {
        // given
        EntityLoaderFactory factory1 = EntityLoaderFactory.getInstance();
        EntityLoaderFactory factory2 = EntityLoaderFactory.getInstance();

        // when, then
        assertThat(factory1).isSameAs(factory2);
    }

    @Test
    @DisplayName("EntityLoaderFactory 는 EntityLoader 를 추가할 수 있다.")
    void testAddLoader() {
        // given
        EntityLoaderFactory factory = EntityLoaderFactory.getInstance();

        // when
        factory.addLoader(TestPerson.class, database);
        EntityLoader<TestPerson> actual = factory.getLoader(TestPerson.class);

        // when, then
        assertThat(actual).isNotNull();
    }

}
