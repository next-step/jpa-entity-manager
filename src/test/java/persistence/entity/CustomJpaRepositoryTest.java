package persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.DatabaseTestBase;

import static org.assertj.core.api.Assertions.assertThat;

class CustomJpaRepositoryTest extends DatabaseTestBase {

    @Test
    @DisplayName("save() 메서드 테스트")
    public void saveTest() {
        CustomJpaRepository<Person, Long> repository = new CustomJpaRepository<>(entityManager);
        Person person = entityManager.find(Person.class, 1L);
        person.setName("new name");

        repository.save(person);
        Person savedPerson = entityManager.find(Person.class, person.getId());

        assertThat(savedPerson).usingRecursiveComparison().isEqualTo(person);
    }

}