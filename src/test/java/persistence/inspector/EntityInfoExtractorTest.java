package persistence.inspector;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.junit.jupiter.api.Test;
import persistence.entity.Person;

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class EntityInfoExtractorTest {

    Class<Person> personClass = Person.class;

    @Test
    void getColumnName() throws NoSuchFieldException {
        Field age = personClass.getDeclaredField("age");

        assertThat(EntityInfoExtractor.getColumnName(age)).isEqualTo("old");
    }

    @Test
    void getTableName() {
        String name = personClass.getAnnotation(Table.class).name();

        assertThat(EntityInfoExtractor.getTableName(personClass)).isEqualTo(name);
    }

    @Test
    void isPrimaryKey() {
        Field realIdField = Arrays.stream(personClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .get();

        assertThat(EntityInfoExtractor.isPrimaryKey(realIdField)).isTrue();
    }

}
