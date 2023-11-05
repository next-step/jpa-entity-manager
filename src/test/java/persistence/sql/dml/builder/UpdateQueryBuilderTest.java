package persistence.sql.dml.builder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.builder.BuilderTest;
import persistence.sql.fixture.PersonFixtureStep3;
import persistence.sql.fixture.PersonInstances;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateQueryBuilderTest extends BuilderTest {

  @Test
  @DisplayName("UPDATE SQL 구문을 생성합니다.")
  public void generateUpdateDML() {
    UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();

    String query = updateQueryBuilder.createUpdateQuery("USERS", List.of("nick_name"),
        List.of("'value2'"), "ID", "6");

    assertThat(query).isEqualTo(
        "UPDATE USERS SET nick_name = 'value2' WHERE ID = 6;");
  }

  @Test
  @DisplayName("UPDATE 실행하고 SELECT 시에 Entity가 변경되어있습니다.")
  public void insertDMLfromEntity() {
    PersonFixtureStep3 afterUpdatedPerson = new PersonFixtureStep3(1L, "value2", 21,
        "sdafij@gmail.com");
    UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();

    String query = updateQueryBuilder.createUpdateQuery("USERS", List.of("nick_name"),
        List.of("'value2'"), "ID", "1");

    jdbcTemplate.execute(query);

    List<Object> people = jdbcTemplate.query("SELECT id,nick_name,old,email FROM USERS WHERE id=1;",
        (rs) ->
            new PersonFixtureStep3(
                rs.getLong("id"),
                rs.getString("nick_name"),
                rs.getInt("old"),
                rs.getString("email")
            ));
    assertThat(people).contains(afterUpdatedPerson);
  }
}
