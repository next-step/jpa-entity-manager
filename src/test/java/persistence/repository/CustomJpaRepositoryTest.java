package persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.JdbcEntityManager;
import persistence.sql.ddl.builder.BuilderTest;
import persistence.sql.fixture.PersonFixtureStep3;
import persistence.sql.fixture.PersonInstances;

public class CustomJpaRepositoryTest extends BuilderTest {

  private CustomJpaRepository<PersonFixtureStep3, Long> customJpaRepository;
  @Test
  @DisplayName("entity를 저장합니다.")
  void saveEntity(){
    customJpaRepository = new CustomJpaRepository<>(new JdbcEntityManager(connection, persistenceContext,
        entityEntry));
    PersonFixtureStep3 person = customJpaRepository.save(PersonInstances.세번째사람);

    PersonFixtureStep3 personFound = customJpaRepository.findById(person, person.getId()).get();

    assertThat(personFound.getId()).isEqualTo(person.getId());
    assertThat(personFound.getName()).isEqualTo(person.getName());
  }

  @Test
  @DisplayName("영속화된 entity를 변경감지로 수정된 entity로 persist 합니다.")
  void persistUpdatedEntity(){
    customJpaRepository = new CustomJpaRepository<>(new JdbcEntityManager(connection, persistenceContext,
        entityEntry));
    PersonFixtureStep3 person = customJpaRepository.save(PersonInstances.세번째사람);

    customJpaRepository.save(person);
    person.setName("사이먼팍");
    customJpaRepository.save(person);

    PersonFixtureStep3 personFound = customJpaRepository.findById(person, person.getId()).get();
    assertThat(personFound.getId()).isEqualTo(person.getId());
    assertThat(personFound.getName()).isEqualTo(person.getName());
  }
}
