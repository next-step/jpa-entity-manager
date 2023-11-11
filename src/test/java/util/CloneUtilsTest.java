package util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.fixture.PersonFixtureStep3;
import persistence.sql.fixture.PersonInstances;

public class CloneUtilsTest {

  private static Class<PersonFixtureStep3> clazz;

  @BeforeAll
  static void setup() {clazz = PersonFixtureStep3.class;
  }

  @Test
  @DisplayName("deep copy 된 object와 car class는 동일하지 않고 동등합니다.")
  void showCloneNotSameObject() {
    PersonFixtureStep3 user = (PersonFixtureStep3) CloneUtils.clone(PersonInstances.두번째사람);

    assertThat(user).isEqualTo(PersonInstances.두번째사람);
    assertThat(user == PersonInstances.두번째사람).isFalse();
  }
}
