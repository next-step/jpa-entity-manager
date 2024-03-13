package persistence.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import persistence.sql.ddl.common.StringConstants;
import persistence.sql.ddl.entity.Person;
import persistence.sql.ddl.entity.Person1;
import persistence.sql.ddl.entity.Person2;
import persistence.sql.ddl.entity.Person3;
import persistence.sql.ddl.entity.Person4;

class EntityTableTest {

    @DisplayName("Get schema name")
    @ParameterizedTest
    @MethodSource("provideFullTableNames")
    void getFullTableName(Class<?> entityClass, String expectedFullTableName) {
        // given
        EntityTable entityTable = new EntityTable(entityClass);

        // when
        String fullTableName = entityTable.getFullTableName();

        // then
        assertEquals(expectedFullTableName, fullTableName);
    }

    @DisplayName("Get schema name")
    @ParameterizedTest
    @MethodSource("provideSchemaNames")
    void getSchemaName(Class<?> entityClass, String expectedSchemaName) {
        // given
        EntityTable entityTable = new EntityTable(entityClass);

        // when
        String schemaName = entityTable.getSchemaName();

        // then
        assertEquals(expectedSchemaName, schemaName);
    }

    @DisplayName("Get schema name")
    @ParameterizedTest
    @MethodSource("provideTableNames")
    void getTableName(Class<?> entityClass, String expectedTableName) {
        // given
        EntityTable entityTable = new EntityTable(entityClass);

        // when
        String tableName = entityTable.getTableName();

        // then
        assertEquals(expectedTableName, tableName);
    }

    /**
     * Provide entity table metadata for testing.
     * @return Stream<Arguments> for entityClass, expectedTableName, expectedSchemaName, expectedFullTableName
     */
    static Stream<Arguments> provideEntityTableMetadata() {
        return Stream.of(
            Arguments.of(Person.class, "users", StringConstants.EMPTY_STRING, "users"),
            Arguments.of(Person1.class, "Person1", StringConstants.EMPTY_STRING, "Person1"),
            Arguments.of(Person2.class, "Person2", StringConstants.EMPTY_STRING, "Person2"),
            Arguments.of(Person3.class, "users", StringConstants.EMPTY_STRING, "users"),
            Arguments.of(Person4.class, "users", "schema", "schema.users")
        );
    }

    static Stream<Arguments> provideTableNames() {
        return provideEntityTableMetadata()
            .map(args -> Arguments.of(args.get()[0], args.get()[1]));
    }

    static Stream<Arguments> provideSchemaNames() {
        return provideEntityTableMetadata()
            .map(args -> Arguments.of(args.get()[0], args.get()[2]));
    }

    static Stream<Arguments> provideFullTableNames() {
        return provideEntityTableMetadata()
            .map(args -> Arguments.of(args.get()[0], args.get()[3]));
    }
}
