package persistence.sql.dml;

import org.junit.jupiter.api.Test;
import persistence.sql.domain.Query;
import persistence.sql.entity.Person;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SelectQueryBuilderTest {


    private final SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder();

    @Test
    void should_create_find_all_query() {
        Query query = selectQueryBuilder.findAll(Person.class);

        assertThat(query.getSql()).isEqualTo("select * from users;");
    }

    @Test
    void should_create_find_by_id_query() {
        Query query = selectQueryBuilder.findById(Person.class, 1l);

        assertThat(query.getSql()).isEqualTo("select * from users where id=1;");
    }

    @Test
    void should_throw_exception_when_id_is_null() {
        assertThatThrownBy(() -> selectQueryBuilder.findById(Person.class, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("database id can not be null");
    }


}
