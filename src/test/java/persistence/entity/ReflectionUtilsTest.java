package persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import persistence.testFixtures.Person;

class ReflectionUtilsTest {

    @Test
    @DisplayName("인스턴스가 생성이 된다")
    void getInstance() {
        Person person = ReflectionUtils.getInstance(Person.class);

        assertNotNull(person);
    }

    @Test
    @DisplayName("인스턴스에 필드에 값을 부여할때 필드 없으면 예외가 발생한다.")
    void setFieldValueException() {
        Person person = ReflectionUtils.getInstance(Person.class);

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> ReflectionUtils.setFieldValue(person, "hia", "hia"));
    }

    @Test
    @DisplayName("인스턴스에 타입이 맞지 않으면 예외가 발생한다.")
    void setFieldTypeException() {
        Person person = ReflectionUtils.getInstance(Person.class);


        assertThatIllegalArgumentException().isThrownBy(() -> {
            ReflectionUtils.setFieldValue(person, "name", 1);
        });
    }

    @Test
    @DisplayName("인스턴스에 필드에 값이 부여 된다.")
    void setFieldValue() {
        //given
        Person person = ReflectionUtils.getInstance(Person.class);

        //when
        ReflectionUtils.setFieldValue(person, "name", "hia");

        //then
        assertThat(person.getName()).isEqualTo("hia");
    }

}
