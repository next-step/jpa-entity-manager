package persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.builder.BuilderTest;
import persistence.sql.fixture.PersonFixtureStep3;
import persistence.sql.fixture.PersonInstances;

import static org.assertj.core.api.Assertions.assertThat;

public class JdbcEntityManagerTest extends BuilderTest {
  @Test
  @DisplayName("EntityManager를 이용해서 find 합니다.")
  public void findEntity() {
    JdbcEntityManager jdbcEntityManager = new JdbcEntityManager(connection, persistenceContext,
        entityEntry);

    PersonFixtureStep3 person = jdbcEntityManager.find(PersonFixtureStep3.class, 2L);

    assertThat(person).isEqualTo(PersonInstances.두번째사람);
    assertThat(entityEntry.getEntityStatus(PersonInstances.두번째사람)).isEqualTo(EntityStatus.MANAGED);
  }

  @Test
  @DisplayName("EntityManager를 이용해서 persist 합니다.")
  public void persistEntity() {
    JdbcEntityManager jdbcEntityManager = new JdbcEntityManager(connection, persistenceContext,
        entityEntry);

    jdbcEntityManager.persist(PersonInstances.세번째사람);

    assertThat(entityEntry.getEntityStatus(PersonInstances.세번째사람)).isEqualTo(EntityStatus.MANAGED);

    PersonFixtureStep3 person = jdbcEntityManager.find(PersonFixtureStep3.class, 3L);

    assertThat(person).isEqualTo(PersonInstances.세번째사람);
    assertThat(entityEntry.getEntityStatus(PersonInstances.세번째사람)).isEqualTo(EntityStatus.LOADING);
  }

  @Test
  @DisplayName("EntityManager를 이용해서 remove 하고 조회하였을 때, 해당 row가 없습니다.")
  public void removeEntity() {
    JdbcEntityManager jdbcEntityManager = new JdbcEntityManager(connection, persistenceContext,
        entityEntry);

    jdbcEntityManager.remove(PersonInstances.첫번째사람);
    PersonFixtureStep3 person = jdbcEntityManager.find(PersonFixtureStep3.class, 1L);

    assertThat(person).isEqualTo(null);
    assertThat(entityEntry.getEntityStatus(PersonInstances.첫번째사람)).isEqualTo(EntityStatus.GONE);
  }
  @Test
  @DisplayName("find시에 1차 캐시에 저장된 entity를 가져온다.")
  public void findEntityWithPersistenceContext() {
    JdbcEntityManager jdbcEntityManager = new JdbcEntityManager(connection, persistenceContext,
        entityEntry);

    jdbcEntityManager.persist(PersonInstances.두번째사람);
    PersonFixtureStep3 person = jdbcEntityManager.find(PersonFixtureStep3.class, 2L);

    assertThat(person).isEqualTo(PersonInstances.두번째사람);
    assertThat(person == PersonInstances.두번째사람).isEqualTo(true);
    assertThat(entityEntry.getEntityStatus(PersonInstances.두번째사람)).isEqualTo(EntityStatus.LOADING);
  }

  @Test
  @DisplayName("persist시에 1차 캐시의 entity와 snapshot의 값이 다르면 더티체킹으로 업데이트한다.")
  public void persistDiffEntityWithPersistenceContext() {
    JdbcEntityManager jdbcEntityManager = new JdbcEntityManager(connection, persistenceContext,
        entityEntry);

    PersonFixtureStep3 person = jdbcEntityManager.find(PersonFixtureStep3.class, 2L);
    person.setName("Sichngpark");

    jdbcEntityManager.persist(person);
    
    PersonFixtureStep3 personInCache = jdbcEntityManager.find(PersonFixtureStep3.class, 2L);

    assertThat(personInCache.getName()).isEqualTo("Sichngpark");
  }
  
}
