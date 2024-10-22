package persistence.sql.dml;

import database.DatabaseServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.config.TestPersistenceConfig;
import persistence.sql.EntityLoaderFactory;
import persistence.sql.QueryBuilderFactory;
import persistence.sql.config.PersistenceConfig;
import persistence.sql.data.QueryType;
import persistence.sql.ddl.TableScanner;
import persistence.sql.dml.impl.SimpleMetadataLoader;
import persistence.sql.node.EntityNode;

import java.util.Set;

public class TestEntityInitialize {
    private static final Logger logger = LoggerFactory.getLogger(TestEntityInitialize.class);
    DatabaseServer server;
    Set<EntityNode<?>> nodes;

    private static void initEntityLoaderFactory(Set<EntityNode<?>> nodes, Database database) {
        EntityLoaderFactory factory = EntityLoaderFactory.getInstance();

        for (EntityNode<?> node : nodes) {
            factory.addLoader(node.entityClass(), database);
        }
    }

    @BeforeEach
    void init() {
        try {
            TestPersistenceConfig config = TestPersistenceConfig.getInstance();
            Database database = config.database();
            server = config.databaseServer();
            server.start();

            TableScanner tableScanner = config.tableScanner();
            nodes = tableScanner.scan("persistence.sql.fixture");
            initEntityLoaderFactory(nodes, database);

            QueryBuilderFactory factory = QueryBuilderFactory.getInstance();
            for (EntityNode<?> node : nodes) {
                String createTableQuery = factory.buildQuery(QueryType.CREATE,
                        new SimpleMetadataLoader<>(node.entityClass()));
                System.out.println("createTableQuery = " + createTableQuery);
                database.executeUpdate(createTableQuery);
            }
        } catch (Exception e) {
            logger.error("Error occurred", e);
        } finally {
            logger.info("Application finished");
        }
    }

    @AfterEach
    void destroy() {
        try {
            PersistenceConfig config = PersistenceConfig.getInstance();
            Database database = config.database();
            QueryBuilderFactory factory = QueryBuilderFactory.getInstance();
            for (EntityNode<?> node : nodes) {
                String createTableQuery = factory.buildQuery(QueryType.DROP,
                        new SimpleMetadataLoader<>(node.entityClass()));
                database.executeUpdate(createTableQuery);
            }
            server.stop();
        } catch (Exception e) {
            logger.error("Error occurred", e);
        }
    }
}
