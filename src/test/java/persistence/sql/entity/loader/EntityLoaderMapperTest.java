package persistence.sql.entity.loader;

import domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.repository.Repository;
import persistence.repository.RepositoryImpl;
import persistence.sql.db.H2Database;
import persistence.sql.dml.conditional.Criteria;
import persistence.sql.dml.query.builder.SelectQueryBuilder;
import persistence.sql.dml.query.clause.ColumnClause;
import persistence.sql.dml.query.clause.WhereClause;
import persistence.sql.entity.EntityMappingTable;
import persistence.sql.entity.model.PrimaryDomainType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class EntityLoaderMapperTest extends H2Database {
    private Connection connection;

    private Person person1;

    @BeforeEach
    void setUp() throws SQLException {
        Repository<Person, Long> personRepository = new RepositoryImpl<>(entityManager, Person.class);

        this.person1 = new Person(1L, "박재성", 10, "jason");

        this.connection = server.getConnection();

        personRepository.deleteAll();
        personRepository.save(person1);
    }


    @DisplayName("디비에서 조회된 컬럼들이 자동으로 객체에 매핑이 된다.")
    @Test
    void mapperTest() throws SQLException {
        PrimaryDomainType primaryDomainType = EntityMappingTable.of(Person.class, person1).getPkDomainTypes();
        final ColumnClause columnClause = new ColumnClause(entityMappingTable.getDomainTypes().getColumnName());
        WhereClause whereClause = new WhereClause(Criteria.fromPkCriterion(primaryDomainType));

        String sql = selectQueryBuilder.toSql(entityMappingTable.getTableName(), columnClause, whereClause);

        Person person = executeQuery(sql);

        assertThat(person).isEqualTo(person1);
    }

    private Person executeQuery(final String sql) {
        try (final ResultSet resultSet = connection.prepareStatement(sql).executeQuery()) {
            resultSet.next();
            return entityLoaderMapper.mapper(Person.class, resultSet);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("SQL 에러가 발생하였습니다.");
        }
    }
}
