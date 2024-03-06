package persistence.sql.dml.query.builder;

import domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.dml.conditional.Criteria;
import persistence.sql.dml.conditional.Criterion;
import persistence.sql.dml.query.clause.WhereClause;
import persistence.sql.entity.EntityMappingTable;
import persistence.sql.entity.model.DomainType;
import persistence.sql.entity.model.TableName;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class DeleteQueryBuilderTest {

    private EntityMappingTable entityMappingTable;

    @BeforeEach
    void setUp() {
        this.entityMappingTable = EntityMappingTable.from(Person.class);
    }

    @DisplayName("삭제 쿼리문을 반환한다.")
    @Test
    void deleteById() {
        DomainType pkDomainTypes = entityMappingTable.getPkDomainTypes();
        Criteria criteria = Criteria.ofCriteria(Collections.singletonList(Criterion.of(pkDomainTypes.getColumnName(), "1")));
        WhereClause whereClause = new WhereClause(criteria);

        DeleteQueryBuilder deleteQueryBuilder = DeleteQueryBuilder.getInstance();

        assertThat(deleteQueryBuilder.toSql(new TableName("person"), whereClause)).isEqualTo("DELETE FROM person WHERE id='1'");
    }

}
