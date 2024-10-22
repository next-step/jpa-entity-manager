package persistence.sql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import persistence.TestUtils;
import persistence.config.TestPersistenceConfig;
import persistence.sql.dml.Database;
import persistence.sql.fixture.TestPerson;
import persistence.sql.loader.EntityLoader;

import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

        // when, then
        assertThatThrownBy(() -> factory.getLoader(TestPerson.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("EntityLoader not found for " + TestPerson.class.getName());

        // and
        factory.addLoader(TestPerson.class, database);

        // when, then
        assertThat(factory.getLoader(TestPerson.class)).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("provideEntityTypeAndExpected")
    @DisplayName("EntityLoaderFactory 는 EntityLoader 를 포함하고 있는지 확인할 수 있다.")
    void testContainsLoader(Class<?> entityType, boolean expected) {
        // given
        EntityLoaderFactory factory = EntityLoaderFactory.getInstance();
        factory.addLoader(TestPerson.class, database);

        // when, then
        assertThat(factory.containsLoader(entityType)).isEqualTo(expected);

    }

    public static Stream<Arguments> provideEntityTypeAndExpected() {
        return Stream.of(
                Arguments.of(TestPerson.class, true),
                Arguments.of(String.class, false)
        );
    }
}
