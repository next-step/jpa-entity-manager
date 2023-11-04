package persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.builder.BuilderTest;
import persistence.sql.fixture.PersonFixtureStep3;
import persistence.sql.fixture.PersonInstances;

public class JdbcEntityLoaderTest extends BuilderTest {
  @Test
  @DisplayName("Loader를 이용해서 find 합니다.")
  public void findEntity() {
    JdbcEntityLoader<PersonFixtureStep3> jdbcEntityLoader = new JdbcEntityLoader<>(PersonFixtureStep3.class, connection);

    PersonFixtureStep3 person = jdbcEntityLoader.load(2L);

    assertThat(person).isEqualTo(PersonInstances.두번째사람);
  }

}
