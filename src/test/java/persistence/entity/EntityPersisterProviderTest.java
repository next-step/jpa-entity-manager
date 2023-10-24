package persistence.entity;

import database.MockJdbcTemplate;
import domain.FixtureEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.dialect.h2.H2Dialect;
import persistence.sql.dml.DmlGenerator;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class EntityPersisterProviderTest {
    private Class<?> fixtureClass;
    private EntityPersisterProvider entityPersisterProvider;

    @BeforeEach
    void setUp() throws SQLException {
        entityPersisterProvider = new EntityPersisterProvider(new MockJdbcTemplate(), new DmlGenerator(new H2Dialect()));
    }

    @Test
    @DisplayName("EntityPersisterProvider 를 통해 해당 클래스의 EntityPersister 를 사용할 수 있다..")
    void entityPersisterProviderTest() {
        fixtureClass = FixtureEntity.WithId.class;
        final EntityPersister entityPersister = entityPersisterProvider.getEntityPersister(fixtureClass);
        assertThat(entityPersister).isNotNull();
    }

    @Test
    @DisplayName("EntityPersisterProvider 를 통해 조회된 같은 타입의 EntityMetadata 는 같은 객체이다.")
    void entityPersisterCacheTest() {
        fixtureClass = FixtureEntity.WithId.class;
        final EntityPersister entityPersister = entityPersisterProvider.getEntityPersister(fixtureClass);
        final EntityPersister entityPersisterV2 = entityPersisterProvider.getEntityPersister(fixtureClass);
        assertThat(entityPersister == entityPersisterV2).isTrue();
    }

}
