package persistence.entity;

import domain.FixtureEntity;
import mock.MockDmlGenerator;
import mock.MockJdbcTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class EntityLoaderProviderTest {
    private Class<FixtureEntity.WithId> fixtureClass;
    private EntityLoaderProvider entityLoaderProvider;

    @BeforeEach
    void setUp() throws SQLException {
        entityLoaderProvider = new EntityLoaderProvider(new MockDmlGenerator(), new MockJdbcTemplate());
    }

    @Test
    @DisplayName("EntityLoaderProvider 를 통해 해당 클래스의 EntityLoader 를 사용할 수 있다..")
    void entityLoaderProviderTest() {
        fixtureClass = FixtureEntity.WithId.class;
        final EntityLoader<FixtureEntity.WithId> entityLoader = entityLoaderProvider.getEntityLoader(fixtureClass);
        assertThat(entityLoader).isNotNull();
    }

    @Test
    @DisplayName("EntityLoaderProvider 를 통해 조회된 같은 타입의 EntityLoader 는 같은 객체이다.")
    void entityLoaderCacheTest() {
        fixtureClass = FixtureEntity.WithId.class;
        final EntityLoader<FixtureEntity.WithId> entityLoader = entityLoaderProvider.getEntityLoader(fixtureClass);
        final EntityLoader<FixtureEntity.WithId> entityLoaderV2 = entityLoaderProvider.getEntityLoader(fixtureClass);
        assertThat(entityLoader == entityLoaderV2).isTrue();
    }

}
