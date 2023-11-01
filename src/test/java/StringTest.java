import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class StringTest {

  @Test
  void convert() {
    int number = 123;
    String numString = String.valueOf(number);
    assertThat(numString).isEqualTo("123");
  }
}
