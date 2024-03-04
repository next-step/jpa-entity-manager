package persistence.sql.dml.query.builder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import domain.Person;
import persistence.sql.dml.conditional.Criteria;
import persistence.sql.dml.conditional.Criterion;
import persistence.sql.dml.query.clause.UpdateColumnClause;
import persistence.sql.dml.query.clause.WhereClause;
import persistence.sql.entity.EntityMappingTable;
import persistence.sql.entity.model.DomainType;
import persistence.sql.entity.model.PrimaryDomainType;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateQueryBuilderTest {
    private Person person;
    private EntityMappingTable entityMappingTable;
    private DomainType pkDomainType;

    @BeforeEach
    void setUp() {
        this.person = new Person(1L, "박재성", 20, "jason@nextstep.com");
        this.entityMappingTable = EntityMappingTable.of(Person.class, person);
        this.pkDomainType = entityMappingTable.getPkDomainTypes();
    }

    @Test
    void updateQueryTest() {
        Criteria criteria = Criteria.fromPkCriterion((PrimaryDomainType) pkDomainType);
        WhereClause whereClause = new WhereClause(criteria);

        UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder(entityMappingTable.getTableName());
        UpdateColumnClause updateColumnClause = UpdateColumnClause.from(entityMappingTable.getDomainTypes());

        assertThat(updateQueryBuilder.toSql(updateColumnClause, whereClause)).isEqualTo("UPDATE Person SET nick_name='박재성',old=20,email='jason@nextstep.com' WHERE id='1'");
    }

}
