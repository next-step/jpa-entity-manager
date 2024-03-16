package persistence.sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static persistence.sql.EntityTableTest.provideEntityTableMetadata;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import persistence.sql.ddl.entity.Person;

class EntityMetadataTest {

    @DisplayName("Get entity table")
    @Test
    void getEntityTable() {
        // given
        EntityMetadata entityMetadata = new EntityMetadata(Person.class);

        // when
        EntityTable entityTable = entityMetadata.getEntityTable();

        // then
        assertThat(entityTable).isNotNull();
    }

    @DisplayName("Get table name")
    @Test
    void getTableName() {
        // given
        EntityMetadata entityMetadata = new EntityMetadata(Person.class);

        // expected
        assertThat(entityMetadata.getTableName()).isEqualTo("users");
    }

    @DisplayName("Get schema name")
    @Test
    void getSchemaName() {
        // given
        EntityMetadata entityMetadata = new EntityMetadata(Person.class);

        // expected
        assertThat(entityMetadata.getSchemaName()).isBlank();
    }

    @DisplayName("Get schema name")
    @ParameterizedTest(name = "entityClass = {0}")
    @MethodSource("provideEntityClassAndSchemaNameAndFullTableName")
    void getTableNameAndSchemaName(Class<?> entityClass, String expectedSchemaName, String expectedFullTableName) {
        // given
        EntityMetadata entityMetadata = new EntityMetadata(entityClass);

        // expected
        assertAll(
            () -> assertThat(entityMetadata.getTableName()).isEqualTo(expectedFullTableName),
            () -> assertThat(entityMetadata.getSchemaName()).isEqualTo(expectedSchemaName)
        );
    }

    @DisplayName("Get entity columns")
    @Test
    void getEntityColumns() {
        // given
        EntityMetadata entityMetadata = new EntityMetadata(Person.class);

        // when
        List<EntityColumn> entityColumns = entityMetadata.getEntityColumns();

        // then
        assertAll(
            () -> assertThat(entityColumns).isNotNull()
        );
    }

    @DisplayName("Get entity column values")
    @Test
    void getEntityColumnValues() {
        // given
        EntityMetadata entityMetadata = new EntityMetadata(Person.class);
        Person person = new Person(1L, "홍길동", 20, "test@gmail.com");

        // when
        List<EntityColumnValue> entityColumnValues = entityMetadata.getEntityColumnValuesFrom(
            person);

        // then
        assertAll(
            () -> assertThat(entityColumnValues).hasSize(4)
        );
    }

    @DisplayName("Get entity id from entity object")
    @Test
    void getEntityIdFrom() {
        // given
        EntityMetadata entityMetadata = new EntityMetadata(Person.class);

        Person givenPerson = new Person(1L, "홍길동", 20, "test@gmail.com");

        // when
        EntityId entityId = entityMetadata.getEntityIdFrom(givenPerson);

        // then
        assertAll(
            () -> assertThat(entityId.getId()).isEqualTo(givenPerson.getId()),
            () -> assertThat(entityId.getEntityClass()).isEqualTo(givenPerson.getClass())
        );
    }

    @DisplayName("Get entity id from entity class and id value object")
    @Test
    void getEntityIdFromEntityClassAndIdValueObject() {
        // given
        Class<?> givenEntityClass = Person.class;
        Long givenPersonId = 1L;
        EntityMetadata entityMetadata = new EntityMetadata(givenEntityClass);

        // when
        EntityId entityId = entityMetadata.getEntityIdFrom(givenEntityClass, givenPersonId);

        // then
        assertAll(
            () -> assertThat(entityId.getId()).isEqualTo(givenPersonId),
            () -> assertThat(entityId.getEntityClass()).isEqualTo(givenEntityClass)
        );
    }

    private static Stream<Arguments> provideEntityClassAndSchemaNameAndFullTableName() {
        return provideEntityTableMetadata()
            .map(args -> Arguments.of(args.get()[0], args.get()[2], args.get()[3]));
    }
}
