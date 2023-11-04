package persistence.meta;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.fixture.PersonFixture2;

@DisplayName("1.2 MetaEntity를 Clazz에서 추출합니다.")
public class MetaEntityTest {
  private static Class<PersonFixture2> person;

  @BeforeAll
  static void setup() {
    person = PersonFixture2.class;
  }

  @Test
  @DisplayName("1.2.1 Clazz가 주어졌을때, Columns를 만들 수 있다.")
  public void createH2TableFromEntity() {
    MetaEntity<PersonFixture2> metaData = MetaEntity.of(person);

    String createClause = metaData.getColumnClause();

    assertAll(
            () -> assertThat(createClause).isEqualTo("nick_name,old,email,index")
    );
  }


}
