package persistence.sql.entity.manager;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import domain.Person;
import persistence.repository.Repository;
import persistence.repository.RepositoryImpl;
import persistence.sql.db.H2Database;
import persistence.sql.dml.conditional.Criteria;
import persistence.sql.dml.conditional.Criterion;
import persistence.sql.dml.query.builder.DeleteQueryBuilder;
import persistence.sql.dml.query.builder.InsertQueryBuilder;
import persistence.sql.dml.query.builder.SelectQueryBuilder;
import persistence.sql.dml.query.builder.UpdateQueryBuilder;
import persistence.sql.dml.query.clause.ColumnClause;
import persistence.sql.dml.query.clause.WhereClause;
import persistence.sql.entity.EntityMappingTable;
import persistence.sql.entity.model.Operators;
import persistence.sql.entity.model.PrimaryDomainType;
import persistence.sql.entity.persister.EntityPersisterImpl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class EntityManagerMapperTest extends H2Database {
    private EntityManagerMapper<Person> entityManagerMapper;
    private Connection connection;
    private SelectQueryBuilder selectQueryBuilder;
    private Repository<Person, Long> personRepository;

    private Person person1;


    @BeforeEach
    void setUp() throws SQLException {
        this.entityManagerMapper = new EntityManagerMapper<>(Person.class);

        final ColumnClause columnClause = new ColumnClause(entityMappingTable.getDomainTypes().getColumnName());
        final EntityManger<Person> entityManager = new EntityManagerImpl<>(
                jdbcTemplate,
                entityManagerMapper,
                new SelectQueryBuilder(entityMappingTable.getTableName(), columnClause),
                new EntityPersisterImpl<>(
                        jdbcTemplate,
                        new InsertQueryBuilder(entityMappingTable.getTableName()),
                        new UpdateQueryBuilder(entityMappingTable.getTableName()),
                        new DeleteQueryBuilder(entityMappingTable.getTableName())
                )
        );

        this.personRepository = new RepositoryImpl<>(entityManager, Person.class);

        this.connection = server.getConnection();
        this.selectQueryBuilder = new SelectQueryBuilder(entityMappingTable.getTableName(), new ColumnClause(entityMappingTable.getDomainTypes().getColumnName()));

        personRepository.deleteAll();

        person1 = new Person(1L, "박재성", 10, "jason");

        personRepository.save(person1);
    }


    @DisplayName("디비에서 조회된 컬럼들이 자동으로 객체에 매핑이 된다.")
    @Test
    void mapperTest() throws SQLException {
        PrimaryDomainType primaryDomainType = EntityMappingTable.of(Person.class, person1).getPkDomainTypes();

        WhereClause whereClause = new WhereClause(Criteria.fromPkCriterion(primaryDomainType));

        String sql = selectQueryBuilder.toSql(whereClause);

        Person person = executeQuery(sql);

        assertThat(person).isEqualTo(person1);
    }

    private Person executeQuery(final String sql) {
        try (final ResultSet resultSet = connection.prepareStatement(sql).executeQuery()) {
            resultSet.next();
            return entityManagerMapper.mapper(resultSet);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("SQL 에러가 발생하였습니다.");
        }
    }
}
