package persistence.sql.dml;

import org.junit.jupiter.api.*;
import persistence.JdbcServerDmlQueryTestSupport;
import persistence.PersonV3FixtureFactory;
import persistence.sql.ddl.PersonV3;
import persistence.sql.dialect.Dialect;
import persistence.sql.dialect.H2Dialect;
import persistence.sql.mapping.Column;
import persistence.sql.mapping.Table;
import persistence.sql.mapping.TableBinder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DefaultDmlQueryBuilderIntegrationTest extends JdbcServerDmlQueryTestSupport {

    private final TableBinder tableBinder = new TableBinder();

    private final Dialect dialect = new H2Dialect();

    private final DmlQueryBuilder dmlQueryBuilder = new DefaultDmlQueryBuilder(dialect);

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute("delete from users");
    }

    @DisplayName("Entity 객체를 insert 쿼리로 생성 후 레코드를 추가한다")
    @Test
    public void insertEntity() throws Exception {
        // given
        final PersonV3 person = new PersonV3(0L, "name", 20, "email@domain.com", 1);
        final Table table = tableBinder.createTable(person);

        final Insert insert = new Insert(table);
        final String insertQuery = dmlQueryBuilder.buildInsertQuery(insert);

        // when
        jdbcTemplate.execute(insertQuery);

        // then
        List<String> result = jdbcTemplate
                .query("SELECT * FROM PUBLIC.users WHERE nick_name = 'name'", resultSet -> resultSet.getString("email"));
        assertThat(result).hasSize(1)
                .contains("email@domain.com");
    }

    @DisplayName("Entity 객체를 findAll 쿼리로 생성 후 레코드를 조회한다")
    @Test
    public void findAll() throws Exception {
        // given
        final PersonV3 person = PersonV3FixtureFactory.generatePersonV3Stub();
        final Table table = tableBinder.createTable(person.getClass());
        final String insertQuery = generateUserTableStubInsertQuery(person);
        jdbcTemplate.execute(insertQuery);

        final Select select = new Select(table);
        final String findAll = dmlQueryBuilder.buildSelectQuery(select);

        // when
        List<String> result = jdbcTemplate
                .query(findAll, resultSet -> resultSet.getString("email"));

        // then
        assertThat(result).hasSize(1)
                .contains("email@domain.com");
    }

    @DisplayName("Entity 객체를 findById 쿼리로 생성 후 레코드를 조회한다")
    @Test
    @Order(0)
    public void findById() throws Exception {
        // given
        final PersonV3 person = PersonV3FixtureFactory.generatePersonV3Stub(1L);
        final Table table = tableBinder.createTable(person);
        final String insertQuery = generateUserTableStubInsertQuery(person);
        jdbcTemplate.execute(insertQuery);

        final Column idColumn = table.getColumn("id");
        final Where where = new Where(idColumn, idColumn.getValue(), LogicalOperator.NONE, new ComparisonOperator(ComparisonOperator.Comparisons.EQ));
        final Select select = new Select(table, List.of(where));
        final String findById = dmlQueryBuilder.buildSelectQuery(select);

        // when
        List<String> result = jdbcTemplate
                .query(findById, resultSet -> resultSet.getString("email"));

        // then
        assertThat(result).hasSize(1)
                .contains("email@domain.com");
    }

    @DisplayName("Entity 객체를 delete 쿼리로 생성 후 레코드를 추가한다")
    @Test
    public void delete() throws Exception {
        // given
        final PersonV3 person = new PersonV3(0L, "name", 20, "email@domain.com", 1);
        final Table table = tableBinder.createTable(person);

        final Column idColumn = table.getColumn("id");
        final Where where = new Where(idColumn, idColumn.getValue(), LogicalOperator.NONE, new ComparisonOperator(ComparisonOperator.Comparisons.EQ));
        final Delete delete = new Delete(table, List.of(where));
        final String deleteQuery = dmlQueryBuilder.buildDeleteQuery(delete);

        // when
        jdbcTemplate.execute(deleteQuery);

        // then
        List<String> tableNames = jdbcTemplate
                .query("SELECT * FROM PUBLIC.users WHERE nick_name = 'name'", resultSet -> resultSet.getString("nick_name"));
        assertThat(tableNames).hasSize(0);
    }

}
