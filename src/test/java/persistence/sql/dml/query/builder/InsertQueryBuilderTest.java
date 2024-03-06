package persistence.sql.dml.query.builder;

import domain.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.dml.query.clause.ColumnClause;
import persistence.sql.dml.query.clause.ValueClause;
import persistence.sql.entity.EntityMappingTable;

import static org.assertj.core.api.Assertions.assertThat;

class InsertQueryBuilderTest {

    @DisplayName("Person 테이블에 들어갈 Insert 쿼리를 반환한다.")
    @Test
    void insertQueryBuild() {
        Person person = new Person(1L, "신성수", 20, "tlstjdtn@nextstep.com");

        final EntityMappingTable entityMappingTable = EntityMappingTable.from(person.getClass());
        final InsertQueryBuilder insertQueryBuilder = InsertQueryBuilder.getInstance();

        ColumnClause columnClause = new ColumnClause(entityMappingTable.getDomainTypes().getColumnName());
        ValueClause valueClause = ValueClause.from(person, entityMappingTable.getDomainTypes());

        String sql = insertQueryBuilder.toSql(entityMappingTable.getTableName(), columnClause, valueClause);

        assertThat(sql).isEqualTo("INSERT INTO Person (id,nick_name,old,email) VALUES (1,'신성수',20,'tlstjdtn@nextstep.com')");
    }
}
