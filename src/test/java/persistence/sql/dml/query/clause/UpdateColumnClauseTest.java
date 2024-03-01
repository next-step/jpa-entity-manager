package persistence.sql.dml.query.clause;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.entity.Person;
import persistence.sql.entity.EntityMappingTable;
import persistence.sql.entity.model.DomainTypes;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateColumnClauseTest {

    private Person person;
    private EntityMappingTable entityMappingTable;

    @BeforeEach
    void setUp() {
        this.person = new Person(1L, "박재성", 20, "jason@nextstep.com");
        this.entityMappingTable = EntityMappingTable.of(Person.class, person);
    }

    @Test
    void updateClauseQuery() {
        DomainTypes domainTypes = entityMappingTable.getDomainTypes();
        UpdateColumnClause updateColumnClause = UpdateColumnClause.from(domainTypes);

        assertThat(updateColumnClause.toSql()).isEqualTo("nick_name='박재성',old=20,email='jason@nextstep.com'");
    }

}
