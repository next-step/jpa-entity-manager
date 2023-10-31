package persistence;

import domain.Person;
import jdbc.RowMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationTest extends IntegrationTestEnvironment {

    @Test
    @DisplayName("쿼리 실행을 통해 데이터를 여러 row 를 넣어 정상적으로 나오는지 확인할 수 있다.")
    void findAllTest() {
        final String query = dmlGenerator.findAll(entityMetadata.getTableName(), entityMetadata.getColumnNames());

        final List<Person> result = jdbcTemplate.query(query, personRowMapper());

        assertThat(result).hasSize(people.size());
    }

    @Test
    @DisplayName("findById 를 통해 원하는 row 를 찾을 수 있다.")
    void findByIdTest() {
        final String query = dmlGenerator.findById(entityMetadata.getTableName(), entityMetadata.getColumnNames(), entityMetadata.getIdColumnName(), 1L);

        final Person result = jdbcTemplate.queryForObject(query, personRowMapper());

        assertThat(result).isNotNull();
    }

    private RowMapper<Person> personRowMapper() {
        return rs -> new Person(rs.getLong("id"), rs.getString("nick_name"), rs.getInt("old"), rs.getString("email"));
    }

}
