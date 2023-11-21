import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.Person;

public class IdentityHashCodeTest {
    @Test
    @DisplayName("객체가 equals 이고 hashCode 가 같은 값을 가져도 identityHashCode 값은 다르다.")
    void test() {
        Person p1 = new Person("1", 1, "1");
        Person p2 = new Person("1", 1, "1");

        assertAll(
            () -> assertThat(p1.equals(p2)).isTrue(),
            () -> assertThat(p1.hashCode()).isEqualTo(p2.hashCode()),
            () -> assertThat(System.identityHashCode(p1)).isNotEqualTo(System.identityHashCode(p2))
        );
    }

    @Test
    @DisplayName("객체가 업데이트 되면 hashCode 값은 변할 수 있지만 identityHashCode 값은 동일하다.")
    void test2() {
        Person p1 = new Person("1", 1, "1");
        int beforeHashCode = p1.hashCode();
        int beforeSystemHashCode = System.identityHashCode(p1);

        p1.setId(23L);
        p1.updateEmail("345");
        int afterHashCode = p1.hashCode();
        int afterSystemHashCode = System.identityHashCode(p1);

        assertAll(
            () -> assertThat(beforeHashCode).isNotEqualTo(afterHashCode),
            () -> assertThat(beforeSystemHashCode).isEqualTo(afterSystemHashCode)
        );
    }
}
