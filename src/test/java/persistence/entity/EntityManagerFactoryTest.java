package persistence.entity;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.fake.FakeDialect;
import persistence.fake.MockJdbcTemplate;

class EntityManagerFactoryTest {

    @Test
    @DisplayName("엔티티 메니저를 생성한다.")
    void createEntityManager() throws Exception {
        //given
        EntityManagerFactory entityManagerFactory = EntityManagerFactory.of("persistence.testFixtures",
                new MockJdbcTemplate(), new FakeDialect());

        //when
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        //then
        assertNotNull(entityManager);
    }

}
