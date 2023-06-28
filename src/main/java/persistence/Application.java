package persistence;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import jdbc.RowMapperImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.BasicEntityManger;
import persistence.entity.EntityManager;
import persistence.model.Person;
import persistence.sql.ddl.builder.DdlQueryBuilder;
import persistence.sql.dml.builder.DeleteQueryBuilder;
import persistence.sql.dml.builder.InsertQueryBuilder;
import persistence.sql.dml.builder.SelectQueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        logger.info("Starting application...");
        try {
            final DatabaseServer server = new H2();
            server.start();

            JdbcTemplate jdbcTemplate = new JdbcTemplate(server.getConnection());
            entityMangerTest(jdbcTemplate);
//            queryTest(jdbcTemplate);

            server.stop();
        } catch (Exception e) {
            logger.error("Error occurred", e);
        } finally {
            logger.info("Application finished");
        }
    }

    private static void entityMangerTest(JdbcTemplate jdbcTemplate) {
        // create
        DdlQueryBuilder ddlQueryBuilder = new DdlQueryBuilder(Person.class);
        String createQuery = ddlQueryBuilder.create();
        jdbcTemplate.execute(createQuery);

        // setup
        Person person = new Person("yohan", 31, "yohan@test.com", 0);
        EntityManager entityManager = new BasicEntityManger(jdbcTemplate);

        // insert
        entityManager.persist(person);

        // select
        Person selectedPerson = entityManager.find(Person.class, person.getId());
        logger.info("추가된 조회된 유저 이름: " + selectedPerson.getName());

        // update
        person.changeName("lee");
        entityManager.merge(person);

        // select
        Person updatedPerson = entityManager.find(Person.class, person.getId());
        logger.info("업데이트된 유저 이름: " + updatedPerson.getName());

        // delete
        entityManager.remove(updatedPerson);
        Person deletedPerson = entityManager.find(Person.class, person.getId());
        logger.info("삭제 여부:" + deletedPerson);

        // drop
        String dropQuery = ddlQueryBuilder.drop();
        jdbcTemplate.execute(dropQuery);
    }

    private static void queryTest(JdbcTemplate jdbcTemplate) throws SQLException {
        Class<Person> personClass = Person.class;
        RowMapper<Person> rowMapper = new RowMapperImpl<>(personClass);

        DdlQueryBuilder ddlQueryBuilder = new DdlQueryBuilder(personClass);
        String createQuery = ddlQueryBuilder.create();
        jdbcTemplate.execute(createQuery);

        Person person = new Person("yohan", 31, "yohan@test.com", 0);
        InsertQueryBuilder insertQueryBuilder = InsertQueryBuilder.INSTANCE;
        String insertQuery = insertQueryBuilder.insert(person);
        jdbcTemplate.execute(insertQuery);

        SelectQueryBuilder selectQueryBuilder = SelectQueryBuilder.INSTANCE;
        String findAllQuery = selectQueryBuilder.findAll(personClass);
        List<Person> persons = jdbcTemplate.query(findAllQuery, rowMapper);
        logger.info("조회 데이터 수: " + persons.size());

        String findByIdQuery = selectQueryBuilder.findById(personClass, persons.get(0).getId());
        Person savedPerson = jdbcTemplate.queryForObject(findByIdQuery, rowMapper);
        logger.info("저장된 Person.id: " + savedPerson.getId());

        DeleteQueryBuilder deleteQueryBuilder = DeleteQueryBuilder.INSTANCE;
        String deleteQuery = deleteQueryBuilder.delete(savedPerson);
        jdbcTemplate.execute(deleteQuery);

        Person deletedPerson = jdbcTemplate.queryForObject(findByIdQuery, rowMapper);
        logger.info("삭제(null) 여부: " + deletedPerson);

        String dropQuery = ddlQueryBuilder.drop();
        jdbcTemplate.execute(dropQuery);
    }
}
