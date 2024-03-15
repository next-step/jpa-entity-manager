package persistence.sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.entity.Person;

class EntityColumnValuesTest {

    @Test
    void getEntityColumnValues() {
        // given
        EntityColumnValues entityValues = new EntityColumnValues(new Person(1L, "John", 30, "test@gmail.com"));

        // when
        List<EntityColumnValue> entityColumnValues = entityValues.getEntityColumnValues();

        List<String> entityColumnQueries = entityColumnValues.stream()
            .map(EntityColumnValue::queryString)
            .collect(Collectors.toList());

        // then
        assertAll(
            () -> assertThat(entityColumnValues).hasSize(4),
            () -> assertThat(entityColumnValues).allMatch(Objects::nonNull),
            () -> assertThat(entityColumnQueries).containsExactly("1", "'John'", "30", "'test@gmail.com'")
        );
    }
}
