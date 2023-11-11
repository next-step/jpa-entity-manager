package persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
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
    List<PersonFixtureStep3> people = jdbcEntityLoader.findAll();
    PersonFixtureStep3 person = jdbcEntityLoader.load(2L).get();

    assertThat(person).isEqualTo(PersonInstances.두번째사람);
  }

  @Test
  @DisplayName("Loader를 이용해서 find 합니다.")
  public void findAllEntity() {
    JdbcEntityLoader<PersonFixtureStep3> jdbcEntityLoader = new JdbcEntityLoader<>(PersonFixtureStep3.class, connection);
    String queryFirst = insertQueryBuilder.createInsertQuery(meta.getTableName(),
        meta.getColumnClause(), meta.getValueClause(PersonInstances.첫번째사람));
    String querySecond = insertQueryBuilder.createInsertQuery(meta.getTableName(),
        meta.getColumnClause(), meta.getValueClause(PersonInstances.두번째사람));
    jdbcTemplate.execute(queryFirst);
    jdbcTemplate.execute(querySecond);

    List<PersonFixtureStep3> people = jdbcEntityLoader.findAll();

    assertThat(people).hasSize(2);
  }
}
