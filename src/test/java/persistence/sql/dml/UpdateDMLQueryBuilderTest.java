package persistence.sql.dml;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.Person;
import persistence.sql.dbms.Dialect;
import persistence.sql.dml.clause.WhereClause;
import persistence.sql.dml.clause.operator.Operator;

import static org.assertj.core.api.Assertions.assertThat;
import static persistence.entity.PersonFixtures.fixture;

class UpdateDMLQueryBuilderTest {

    @DisplayName("UpdateDMLQueryBuilder build 기본 구조")
    @Test
    void build() {
        Person person = fixture(1L, "name1", 20, "email1");
        UpdateDMLQueryBuilder<Person> updateDMLQueryBuilder = new UpdateDMLQueryBuilder(Dialect.H2, person);

        assertThat(updateDMLQueryBuilder.build()).isEqualTo("UPDATE USERS \n" +
                "SET NICK_NAME = 'name1', OLD = 20, EMAIL = 'email1' \n" +
                ";");
    }

    @DisplayName("UpdateDMLQueryBuilder build where 구문 추가")
    @Test
    void build_where_clause() {
        Person person = fixture(1L, "name1", 20, "email1");
        UpdateDMLQueryBuilder<Person> updateDMLQueryBuilder = new UpdateDMLQueryBuilder<>(Dialect.H2, person)
                .where(WhereClause.of("ID", 1L, Operator.EQUALS));

        assertThat(updateDMLQueryBuilder.build()).isEqualTo("UPDATE USERS \n" +
                "SET NICK_NAME = 'name1', OLD = 20, EMAIL = 'email1' \n" +
                "WHERE ID = 1;");
    }
}
