package persistence;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import domain.LegacyPerson;
import persistence.sql.ddl.query.builder.CreateQueryBuilder;
import persistence.sql.ddl.query.builder.DropQueryBuilder;
import persistence.sql.dialect.Dialect;
import persistence.sql.dialect.DialectResolutionInfo;
import persistence.sql.dialect.database.Database;
import persistence.sql.entity.EntityMappingTable;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        logger.info("Starting application...");
        try {
            final DatabaseServer server = new H2();
            server.start();

            final JdbcTemplate jdbcTemplate = new JdbcTemplate(server.getConnection());

            // DB 정보
            DialectResolutionInfo dialectResolutionInfo = new DialectResolutionInfo(server.getConnection().getMetaData());
            // 방언 설정
            Dialect dialect = Database.from(dialectResolutionInfo).getDialectSupplier().get();

            final EntityMappingTable entityMappingTable = EntityMappingTable.from(LegacyPerson.class);
            String createTableSql = CreateQueryBuilder.of(entityMappingTable, dialect.getTypeMapper(), dialect.getConstantTypeMapper()).toSql();
            String dropTableSql = new DropQueryBuilder(entityMappingTable).toSql();

            logger.debug(createTableSql);
            logger.debug(dropTableSql);
            jdbcTemplate.execute(createTableSql);
            jdbcTemplate.execute(dropTableSql);

            server.stop();
        } catch (Exception e) {
            logger.error("Error occurred", e);
        } finally {
            logger.info("Application finished");
        }
    }
}
