package persistence.sql.ddl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.Person;
import persistence.sql.ddl.dialect.Dialect;
import persistence.sql.ddl.dialect.H2Dialect;

import static org.assertj.core.api.Assertions.assertThat;

public class EntityDefinitionBuilderTest {

    private static EntityDefinitionBuilder entityDefinitionBuilder;

    @BeforeAll
    static void setUp() {
        EntityMetadata entityMetadata = EntityMetadata.of(Person.class);
        entityDefinitionBuilder = new EntityDefinitionBuilder(entityMetadata);
    }

    @Test
    @DisplayName("Person 엔터티 create 쿼리 만들기")
    public void createQueryTest() {
        Dialect dialect = new H2Dialect();
        String query = entityDefinitionBuilder.create(dialect);

        assertThat(query).isEqualTo("CREATE TABLE users (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "nick_name VARCHAR," +
                "old INT," +
                "email VARCHAR NOT NULL);");
    }

    @Test
    @DisplayName("Person 엔터티 drop쿼리 만들기")
    public void dropQueryTest() {
        String query = entityDefinitionBuilder.drop();

        assertThat(query).isEqualTo("DROP TABLE users;");
    }

}
