package persistence.entity;

import domain.FixtureEntity;
import jdbc.JdbcTemplate;
import mock.MockDmlGenerator;
import mock.MockJdbcTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.Application;
import persistence.sql.dml.DmlGenerator;

import java.sql.SQLException;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class EntityPersisterProviderTest {
    private Class<?> fixtureClass;
    private EntityPersisterProvider entityPersisterProvider;

    @BeforeEach
    void setUp() throws SQLException {
        entityPersisterProvider = new EntityPersisterProvider(initEntityPersisters(new MockDmlGenerator(), new MockJdbcTemplate()));
    }

    private Map<Class<?>, EntityPersister> initEntityPersisters(final DmlGenerator dmlGenerator, final JdbcTemplate jdbcTemplate) {
        final EntityScanner entityScanner = new EntityScanner(Application.class);
        return entityScanner.getEntityClasses().stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        clazz -> new EntityPersister(clazz, dmlGenerator, jdbcTemplate)
                ));
    }

    @Test
    @DisplayName("EntityPersisterProvider 를 통해 해당 클래스의 EntityPersister 를 사용할 수 있다..")
    void entityPersisterProviderTest() {
        fixtureClass = FixtureEntity.WithId.class;
        final EntityPersister entityPersister = entityPersisterProvider.getEntityPersister(fixtureClass);
        assertThat(entityPersister).isNotNull();
    }

    @Test
    @DisplayName("EntityPersisterProvider 를 통해 조회된 같은 타입의 EntityPersister 는 같은 객체이다.")
    void entityPersisterCacheTest() {
        fixtureClass = FixtureEntity.WithId.class;
        final EntityPersister entityPersister = entityPersisterProvider.getEntityPersister(fixtureClass);
        final EntityPersister entityPersisterV2 = entityPersisterProvider.getEntityPersister(fixtureClass);
        assertThat(entityPersister == entityPersisterV2).isTrue();
    }

}
