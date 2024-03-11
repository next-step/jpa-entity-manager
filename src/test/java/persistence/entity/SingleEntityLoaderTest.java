package persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.JdbcServerDmlQueryTestSupport;
import persistence.PersonV3FixtureFactory;
import persistence.entity.loader.EntityLoader;
import persistence.entity.loader.SingleEntityLoader;
import persistence.sql.ddl.PersonV3;
import persistence.sql.dialect.Dialect;
import persistence.sql.dialect.H2Dialect;
import persistence.sql.dml.DefaultDmlQueryBuilder;
import persistence.sql.mapping.TableBinder;

import static org.assertj.core.api.Assertions.assertThat;

class SingleEntityLoaderTest extends JdbcServerDmlQueryTestSupport {

    private final TableBinder tableBinder = new TableBinder();
    private final Dialect dialect = new H2Dialect();
    private final DefaultDmlQueryBuilder dmlQueryBuilder = new DefaultDmlQueryBuilder(dialect);

    private final EntityLoader entityLoader = new SingleEntityLoader(tableBinder, dmlQueryBuilder, jdbcTemplate);

    @DisplayName("클래스 정보로 엔티티를 조회한다.")
    @Test
    public void load() throws Exception {
        // given
        final Class<PersonV3> clazz = PersonV3.class;
        final long key = 1L;
        final PersonV3 person = PersonV3FixtureFactory.generatePersonV3Stub();
        final String insertQuery = generateUserTableStubInsertQuery(person);

        jdbcTemplate.execute(insertQuery);

        // when
        final PersonV3 entity = entityLoader.load(clazz, key);

        // then
        assertThat(entity).isNotNull()
                .extracting("id", "name", "age", "email")
                .contains(key, person.getName(), person.getAge(), person.getEmail());
    }

}
