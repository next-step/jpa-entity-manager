package sample.repository;

import java.sql.Connection;
import java.util.List;
import jdbc.JdbcTemplate;
import persistence.sql.dml.query.SelectQuery;
import persistence.sql.dml.query.WhereCondition;
import persistence.sql.dml.query.builder.SelectQueryBuilder;
import sample.domain.Person;

public class PersonRepository implements Repository<Person, Long> {

    private final JdbcTemplate jdbcTemplate;

    public PersonRepository(Connection connection) {
        this.jdbcTemplate = new JdbcTemplate(connection);
    }

    @Override
    public List<Person> findAll() {
        SelectQuery query = new SelectQuery(Person.class);
        SelectQueryBuilder queryBuilder = SelectQueryBuilder.builder()
                .select(query.columnNames())
                .from(query.tableName());
        String queryString = queryBuilder.build();

        return jdbcTemplate.query(queryString, (resultSet -> new Person(
                resultSet.getLong("id"),
                resultSet.getString("nick_name"),
                resultSet.getInt("old"),
                resultSet.getString("email")
        )));
    }

    @Override
    public Person findById(Long id) {
        SelectQuery query = new SelectQuery(Person.class);
        SelectQueryBuilder queryBuilder = SelectQueryBuilder.builder()
                .select(query.columnNames())
                .from(query.tableName())
                .where(List.of(new WhereCondition("id", "=", id)));
        String queryString = queryBuilder.build();

        return jdbcTemplate.queryForObject(queryString, (resultSet -> new Person(
                resultSet.getLong("id"),
                resultSet.getString("nick_name"),
                resultSet.getInt("old"),
                resultSet.getString("email")
        )));
    }

}
