package persistence;

import database.DatabaseServer;
import database.H2;
import domain.Person;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import persistence.core.EntityMetadata;
import persistence.core.EntityMetadataProvider;
import persistence.core.PersistenceEnvironment;
import persistence.dialect.h2.H2Dialect;
import persistence.sql.ddl.DdlGenerator;
import persistence.sql.dml.DmlGenerator;
import persistence.util.ReflectionUtils;

import java.sql.SQLException;
import java.util.List;

public abstract class IntegrationTestEnvironment {
    private DatabaseServer server;
    protected DdlGenerator ddlGenerator;
    protected DmlGenerator dmlGenerator;
    protected EntityMetadata<?> entityMetadata;
    protected JdbcTemplate jdbcTemplate;
    protected PersistenceEnvironment persistenceEnvironment;
    protected List<Person> people;

    @BeforeEach
    void integrationSetUp() throws SQLException {
        server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        persistenceEnvironment = new PersistenceEnvironment(server, new H2Dialect());
        ddlGenerator = new DdlGenerator(persistenceEnvironment.getDialect());
        dmlGenerator = new DmlGenerator(persistenceEnvironment.getDialect());

        entityMetadata = EntityMetadataProvider.getInstance().getEntityMetadata(Person.class);
        final String createDdl = ddlGenerator.generateCreateDdl(entityMetadata);
        jdbcTemplate.execute(createDdl);
        people = createDummyUsers();

        people.forEach(person -> {
            final List<String> columnNames = entityMetadata.getInsertableColumnNames();
            final List<Object> values = ReflectionUtils.getFieldValues(person, entityMetadata.getInsertableColumnFieldNames());
            jdbcTemplate.execute(dmlGenerator.insert(entityMetadata.getTableName(), columnNames, values));
        });
    }

    @AfterEach
    void integrationTearDown() {
        final String dropDdl = ddlGenerator.generateDropDdl(entityMetadata);
        jdbcTemplate.execute(dropDdl);
        server.stop();
    }

    private static List<Person> createDummyUsers() {
        final Person test00 = new Person("test00", 0, "test00@gmail.com");
        final Person test01 = new Person("test01", 10, "test01@gmail.com");
        final Person test02 = new Person("test02", 20, "test02@gmail.com");
        final Person test03 = new Person("test03", 30, "test03@gmail.com");
        return List.of(test00, test01, test02, test03);
    }

}
