package persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.builder.BuilderTest;
import persistence.sql.fixture.PersonFixtureStep3;
import persistence.sql.fixture.PersonInstances;

public class JdbcEntityPersisterTest extends BuilderTest {

  @Test
  @DisplayName("Persister를 이용해서 insert 합니다.")
  public void persisterInsertEntity() {
    JdbcEntityManager jdbcEntityManager = new JdbcEntityManager(jdbcTemplate, connection);
    JdbcEntityPersister persister = new JdbcEntityPersister(PersonFixtureStep3.class, connection);
    PersonFixtureStep3 네번째사람 = new PersonFixtureStep3(4L, "지미 헨드릭스", 24, "sdafij@gmail.com");


    persister.insert(네번째사람);

    PersonFixtureStep3 person = jdbcEntityManager.find(PersonFixtureStep3.class, 4L);

    assertThat(person).isEqualTo(네번째사람);
  }
  @Test
  @DisplayName("Persister를 이용해서 update 합니다.")
  public void persisterUpdateEntity() {
    JdbcEntityManager jdbcEntityManager = new JdbcEntityManager(jdbcTemplate, connection);
    JdbcEntityPersister persister = new JdbcEntityPersister(PersonFixtureStep3.class, connection);
    PersonFixtureStep3 업데이트된세번째사람 = new PersonFixtureStep3(3L, "헨드릭스", 24, "sdafij@gmail.com");


    persister.insert(PersonInstances.세번째사람);
    boolean execute = persister.update(업데이트된세번째사람, "name");

    PersonFixtureStep3 person = jdbcEntityManager.find(PersonFixtureStep3.class, 3L);

    assertThat(execute).isEqualTo(false);
    assertThat(person).isEqualTo(업데이트된세번째사람);
  }

  @Test
  @DisplayName("Persister를 이용해서 delete 합니다.")
  public void persisterDeleteEntity() {
    JdbcEntityManager jdbcEntityManager = new JdbcEntityManager(jdbcTemplate, connection);
    JdbcEntityPersister persister = new JdbcEntityPersister(PersonFixtureStep3.class, connection);

    persister.delete(PersonInstances.두번째사람);

    Throwable thrown = catchThrowable(() -> {
      jdbcEntityManager.find(PersonFixtureStep3.class, 2L);
    });
    assertThat(thrown).isInstanceOf(RuntimeException.class);
  }
}
