package persistence.sql.dml.query.builder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import domain.Person;
import persistence.sql.dml.query.clause.ColumnClause;
import persistence.sql.dml.query.clause.WhereClause;
import persistence.sql.entity.EntityMappingTable;
import persistence.sql.dml.conditional.Criteria;
import persistence.sql.dml.conditional.Criterion;
import persistence.sql.entity.model.DomainType;
import persistence.sql.entity.model.Operators;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SelectQueryBuilderTest {

    private EntityMappingTable entityMappingTable;
    private SelectQueryBuilder selectQueryBuilder;

    @BeforeEach
    void setUp() {
        this.entityMappingTable = EntityMappingTable.from(Person.class);
        this.selectQueryBuilder = new SelectQueryBuilder(entityMappingTable.getTableName(), new ColumnClause(entityMappingTable.getDomainTypes().getColumnName()));
    }

    @DisplayName("조건문 없는 SELECT문을 반환한다.")
    @Test
    void sqlTest() {
        WhereClause whereClause = new WhereClause(Criteria.emptyInstance());

        assertThat(selectQueryBuilder.toSql(whereClause)).isEqualTo("SELECT id,nick_name,old,email FROM Person ");
    }

    @DisplayName("조건문 있는 SELECT문을 반환한다.")
    @Test
    void whereSqlTest() {
        DomainType domainType = entityMappingTable.getPkDomainTypes();
        Criteria criteria = Criteria.ofCriteria(Map.of(domainType.getColumnName(), "1"));
        WhereClause whereClause = new WhereClause(criteria);

        assertThat(selectQueryBuilder.toSql(whereClause)).isEqualTo("SELECT id,nick_name,old,email FROM Person WHERE id='1'");
    }

}
