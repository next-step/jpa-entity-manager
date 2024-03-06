package persistence.sql.db;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.BeforeAll;
import domain.Person;
import persistence.sql.ddl.query.builder.CreateQueryBuilder;
import persistence.sql.ddl.query.builder.DropQueryBuilder;
import persistence.sql.dialect.Dialect;
import persistence.sql.dialect.DialectResolutionInfo;
import persistence.sql.dialect.database.Database;
import persistence.sql.dml.query.builder.DeleteQueryBuilder;
import persistence.sql.dml.query.builder.InsertQueryBuilder;
import persistence.sql.dml.query.builder.SelectQueryBuilder;
import persistence.sql.dml.query.builder.UpdateQueryBuilder;
import persistence.sql.entity.EntityMappingTable;
import persistence.sql.entity.loader.EntityLoader;
import persistence.sql.entity.loader.EntityLoaderImpl;
import persistence.sql.entity.loader.EntityLoaderMapper;
import persistence.sql.entity.manager.EntityManager;
import persistence.sql.entity.manager.EntityManagerImpl;
import persistence.sql.entity.persister.EntityPersister;
import persistence.sql.entity.persister.EntityPersisterImpl;

import java.sql.SQLException;

public abstract class H2Database {

    protected static EntityMappingTable entityMappingTable;

    protected static DatabaseServer server;

    protected static JdbcTemplate jdbcTemplate;

    protected static SelectQueryBuilder selectQueryBuilder;
    protected static InsertQueryBuilder insertQueryBuilder;
    protected static UpdateQueryBuilder updateQueryBuilder;
    protected static DeleteQueryBuilder deleteQueryBuilder;


    protected static EntityLoaderMapper entityLoaderMapper;
    protected static EntityLoader entityLoader;
    protected static EntityPersister entityPersister;

    protected static EntityManager entityManager;

    @BeforeAll
    static void setUpAll() throws SQLException {
        server = new H2();
        jdbcTemplate = new JdbcTemplate(server.getConnection());

        selectQueryBuilder = SelectQueryBuilder.getInstance();
        insertQueryBuilder = InsertQueryBuilder.getInstance();
        updateQueryBuilder = UpdateQueryBuilder.getInstance();
        deleteQueryBuilder = DeleteQueryBuilder.getInstance();
        entityLoaderMapper = EntityLoaderMapper.getInstance();

        entityPersister = new EntityPersisterImpl(
                jdbcTemplate,
                insertQueryBuilder,
                updateQueryBuilder,
                deleteQueryBuilder);
        entityLoader = new EntityLoaderImpl(
                jdbcTemplate,
                entityLoaderMapper,
                selectQueryBuilder);

        entityManager = new EntityManagerImpl(entityLoader, entityPersister);

        createTable();
    }

    private static void createTable() throws SQLException {
        DialectResolutionInfo dialectResolutionInfo = new DialectResolutionInfo(server.getConnection().getMetaData());
        Dialect dialect = Database.from(dialectResolutionInfo).getDialectSupplier().get();
        entityMappingTable = EntityMappingTable.from(Person.class);
        CreateQueryBuilder createQueryBuilder = CreateQueryBuilder.of(entityMappingTable, dialect.getTypeMapper(), dialect.getConstantTypeMapper());
        DropQueryBuilder dropQueryBuilder = new DropQueryBuilder(entityMappingTable);

        jdbcTemplate.execute(dropQueryBuilder.toSql());
        jdbcTemplate.execute(createQueryBuilder.toSql());
    }
}
