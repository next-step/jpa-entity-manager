package persistence.sql.dml.query.builder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.entity.Person;
import persistence.sql.dml.conditional.Criteria;
import persistence.sql.dml.conditional.Criterion;
import persistence.sql.entity.EntityMappingTable;
import persistence.sql.entity.model.DomainType;

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
        Criterion criterion = Criterion.of(pkDomainType.getColumnName(), pkDomainType.getValue().toString());
        Criteria criteria = new Criteria(Collections.singletonList(criterion));

        UpdateQueryBuilder queryBuilder = UpdateQueryBuilder.of(entityMappingTable, criteria);

        assertThat(queryBuilder.toSql()).isEqualTo("UPDATE Person SET nick_name='박재성',old=20,email='jason@nextstep.com' where id='1'");
    }

}
