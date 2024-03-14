package persistence.sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static persistence.sql.EntityTableTest.provideEntityTableMetadata;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import persistence.sql.ddl.entity.Person;

class MetadataTest {

    @DisplayName("Get entity table")
    @Test
    void getEntityTable() {
        // given
        Metadata metadata = new Metadata(Person.class);

        // when
        EntityTable entityTable = metadata.getEntityTable();

        // then
        assertThat(entityTable).isNotNull();
    }

    @DisplayName("Get table name")
    @Test
    void getTableName() {
        // given
        Metadata metadata = new Metadata(Person.class);

        // expected
        assertThat(metadata.getTableName()).isEqualTo("users");
    }

    @DisplayName("Get schema name")
    @Test
    void getSchemaName() {
        // given
        Metadata metadata = new Metadata(Person.class);

        // expected
        assertThat(metadata.getSchemaName()).isBlank();
    }

    @DisplayName("Get schema name")
    @ParameterizedTest(name = "entityClass = {0}")
    @MethodSource("provideEntityClassAndSchemaNameAndFullTableName")
    void getTableNameAndSchemaName(Class<?> entityClass, String expectedSchemaName, String expectedFullTableName) {
        // given
        Metadata metadata = new Metadata(entityClass);

        // expected
        assertAll(
            () -> assertThat(metadata.getTableName()).isEqualTo(expectedFullTableName),
            () -> assertThat(metadata.getSchemaName()).isEqualTo(expectedSchemaName)
        );
    }

    @DisplayName("Get entity columns")
    @Test
    void getEntityColumns() {
        // given
        Metadata metadata = new Metadata(Person.class);

        // when
        EntityColumns entityColumns = metadata.getEntityColumns();

        // then
        assertAll(
            () -> assertThat(entityColumns).isNotNull()
        );
    }

    @DisplayName("Get entity column values")
    @Test
    void getEntityColumnValues() {
        // given
        Metadata metadata = new Metadata(Person.class);
        Person person = new Person(1L, "홍길동", 20, "test@gmail.com");

        // when
        EntityColumnValues entityColumnValues = metadata.getEntityValuesFrom(person);

        // then
        assertAll(
            () -> assertThat(entityColumnValues).isNotNull()
        );
    }

    private static Stream<Arguments> provideEntityClassAndSchemaNameAndFullTableName() {
        return provideEntityTableMetadata()
            .map(args -> Arguments.of(args.get()[0], args.get()[2], args.get()[3]));
    }
}
