package persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;

import domain.Person;
import domain.SelectPerson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.exception.InvalidContextException;

class PersistenceContextImplTest {

    private PersistenceContextImpl persistenceContext;

    @BeforeEach
    void init() {
        persistenceContext = new PersistenceContextImpl();
    }

    @Nested
    @DisplayName("영속성 컨텍스트에서 데이터를 저장합니다.")
    class addEntity {

        @Test
        @DisplayName("성공적으로 데이터를 저장합니다.")
        void success() {
            //given
            final Long id = 9898L;
            final String name = "name";
            final Integer age = 30;
            final String email = "email";
            final int index = 3;

            final Integer key = id.hashCode();

            SelectPerson person = new SelectPerson(id, name, age, email, index);

            //when
            persistenceContext.addEntity(key, id, person);

            SelectPerson result = (SelectPerson) 영속성_컨텍스트에서_데이터를_가져온다(key);

            //then
            assertSoftly(softAssertions -> {
                softAssertions.assertThat(result.getId()).isEqualTo(id);
                softAssertions.assertThat(result.getName()).isEqualTo(name);
                softAssertions.assertThat(result.getAge()).isEqualTo(age);
                softAssertions.assertThat(result.getEmail()).isEqualTo(email);
                softAssertions.assertThat(result.getIndex()).isNull();
            });
        }

        @Test
        @DisplayName("서로 다른 객체가 영속성 컨텍스트에 저장이 된다")
        void differentEntitySave() {
            //given
            final Long id = 333L;
            SelectPerson selectPerson = new SelectPerson(id, "name", 30, "email", 3);
            Person person = new Person(id, "name", 30, "email", 3);

            final int selectPersonKey = selectPerson.hashCode();
            final int personKey = person.hashCode();

            //when
            영속성_컨텍스트에서_데이터를_저장한다(selectPersonKey, id, selectPerson);
            영속성_컨텍스트에서_데이터를_저장한다(personKey, id, person);

            //then
            assertSoftly(softAssertions -> {
                softAssertions.assertThat(영속성_컨텍스트에서_데이터를_가져온다(selectPersonKey)).isNotNull();
                softAssertions.assertThat(영속성_컨텍스트에서_데이터를_가져온다(personKey)).isNotNull();
            });
        }
    }

    @Nested
    @DisplayName("영속성 컨텍스트에서 데이터를 가져옵니다.")
    class getEntity {

        @Test
        @DisplayName("성공적으로 데이터를 가져옵니다.")
        void success() {
            //given
            final Long id = 3333L;
            final String name = "name";
            final Integer age = 30;
            final String email = "email";
            final int index = 3;

            final Integer key = id.hashCode();

            SelectPerson person = new SelectPerson(id, name, age, email, index);
            영속성_컨텍스트에서_데이터를_저장한다(key, id, person);

            //when
            SelectPerson result = (SelectPerson) persistenceContext.getEntity(key);

            //then
            assertSoftly(softAssertions -> {
                softAssertions.assertThat(result.getId()).isEqualTo(id);
                softAssertions.assertThat(result.getName()).isEqualTo(name);
                softAssertions.assertThat(result.getAge()).isEqualTo(age);
                softAssertions.assertThat(result.getEmail()).isEqualTo(email);
                softAssertions.assertThat(result.getIndex()).isNull();
            });
        }

        @Test
        @DisplayName("영속성 컨텍스트에 저장되지 않은 값을 가져오면 null 반환")
        void returnNull() {
            //given
            final Integer key = -93939393;

            //when
            SelectPerson result = (SelectPerson) persistenceContext.getEntity(key);

            //then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("동일성에 관련한 테스트 코드 작성하기")
        void equals() {
            //given
            final Long id = 2233L;
            SelectPerson selectPerson = new SelectPerson(id, "name", 30, "email", 3);

            //when
            SelectPerson firstResult = (SelectPerson) 영속성_컨텍스트에서_데이터를_가져온다(selectPerson.hashCode());
            SelectPerson secondResult = (SelectPerson) 영속성_컨텍스트에서_데이터를_가져온다(selectPerson.hashCode());

            //then
            assertThat(firstResult).isEqualTo(secondResult);
        }
    }

    @Nested
    @DisplayName("영속성 컨텍스트에서 데이터를 삭제합니다.")
    class removeEntity {

        @Test
        @DisplayName("성공적으로 데이터를 삭제합니다.")
        void success() {
            //given
            final Long id = 5555L;
            final Integer key = id.hashCode();

            SelectPerson person = new SelectPerson(id, "name", 30, "email", 3);
            영속성_컨텍스트에서_데이터를_저장한다(key, id, person);

            //when
            persistenceContext.removeEntity(key);

            SelectPerson result = (SelectPerson) persistenceContext.getEntity(key);

            //then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("존재하지 않는 데이터를 삭제하려고 할 때 오류 출력")
        void notFound() {
            //given
            final Integer key = 33312;

            //when & then
            assertThrows(InvalidContextException.class, () -> persistenceContext.removeEntity(key));
        }
    }

    private void 영속성_컨텍스트에서_데이터를_저장한다(Integer key, Object id, Object entity) {
        persistenceContext.addEntity(key, id, entity);
    }

    private Object 영속성_컨텍스트에서_데이터를_가져온다(Integer id) {
        return persistenceContext.getEntity(id);
    }
}
