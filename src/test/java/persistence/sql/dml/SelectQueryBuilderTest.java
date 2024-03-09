package persistence.sql.dml;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.dml.domain.Person;
import persistence.sql.meta.EntityMetaCreator;
import persistence.sql.meta.Table;
import persistence.sql.meta.simple.SimpleEntityMetaCreator;

import static org.assertj.core.api.Assertions.assertThat;

class SelectQueryBuilderTest {

    private SelectQueryBuilder selectQueryBuilder;
    private EntityMetaCreator entityMetaCreator;
    @BeforeEach
    void init() {
        selectQueryBuilder = new SelectQueryBuilder();
        entityMetaCreator = new SimpleEntityMetaCreator();
    }

    @DisplayName("Person객체를 통해 findAll 기능을 구현한다.")
    @Test
    void dml_findAll_create() {
        final Table table = entityMetaCreator.createByClass(Person.class);;

        String findAllQuery = selectQueryBuilder.createFindAllQuery(table);

        String expected = "select id, nick_name, old, email from users";
        assertThat(findAllQuery).isEqualTo(expected);
    }

    @DisplayName("Person객체를 통해 findById 기능을 구현한다.")
    @Test
    void dml_findById_create() {
        final Table table = entityMetaCreator.createByClass(Person.class);;

        String findByIdQuery = selectQueryBuilder.createFindByIdQuery(table, 1L);

        String expected = "select id, nick_name, old, email from users where id = 1L";
        assertThat(findByIdQuery).isEqualTo(expected);
    }
}