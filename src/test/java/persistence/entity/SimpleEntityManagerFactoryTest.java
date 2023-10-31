package persistence.entity;

import mock.MockPersistenceEnvironment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.Application;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleEntityManagerFactoryTest {

    @Test
    @DisplayName("EntityManagerFactory 를 이용해 EntityManager 를 생성할 수 있다.")
    void test() {
        final EntityScanner entityScanner = new EntityScanner(Application.class);
        final EntityManagerFactory entityManagerFactory = new SimpleEntityManagerFactory(entityScanner, new MockPersistenceEnvironment());

        final EntityManager entityManager = entityManagerFactory.createEntityManager();

        assertThat(entityManager).isNotNull();
    }
}
