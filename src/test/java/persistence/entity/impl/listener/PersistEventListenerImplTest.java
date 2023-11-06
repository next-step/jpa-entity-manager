package persistence.entity.impl.listener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import database.DatabaseServer;
import database.H2;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.EntityManager;
import persistence.entity.EventListener;
import persistence.entity.EventSource;
import persistence.entity.impl.EntityManagerImpl;
import persistence.entity.impl.context.DefaultPersistenceContext;
import persistence.entity.impl.event.PersistEvent;
import persistence.entity.impl.store.EntityPersisterImpl;
import persistence.sql.ddl.generator.CreateDDLQueryGenerator;
import persistence.sql.ddl.generator.DropDDLQueryGenerator;
import persistence.sql.dialect.H2ColumnType;
import persistence.sql.dml.Database;
import persistence.sql.dml.JdbcTemplate;

@DisplayName("PersistEventListener 테스트")
class PersistEventListenerImplTest {
    private DatabaseServer server;

    private Database jdbcTemplate;

    private EventSource eventSource;

    private EventListener persistEventListener;

    private EntityManager entityManager;

    @BeforeEach
    void setUp() throws SQLException {
        server = new H2();
        server.start();

        Connection connection = server.getConnection();

        final EntityPersisterImpl persister = new EntityPersisterImpl(connection);
        final H2ColumnType columnType = new H2ColumnType();

        persistEventListener = new PersistEventListenerImpl(persister, columnType);
        final DefaultPersistenceContext persistenceContext = new DefaultPersistenceContext(columnType);
        eventSource = persistenceContext;
        entityManager = new EntityManagerImpl(connection, columnType, persistenceContext);
        jdbcTemplate = new JdbcTemplate(connection);
        CreateDDLQueryGenerator createDDLQueryGenerator = new CreateDDLQueryGenerator(columnType);
        jdbcTemplate.execute(createDDLQueryGenerator.create(PersistEventEntity.class));
    }

    @AfterEach
    void tearDown() throws Exception {
        DropDDLQueryGenerator dropDDLQueryGenerator = new DropDDLQueryGenerator(new H2ColumnType());
        jdbcTemplate.execute(dropDDLQueryGenerator.drop(PersistEventEntity.class));
        entityManager.close();
        server.stop();
    }

    @Test
    @DisplayName("삽입 이벤트를 수신하면 해당 엔티티는 적재된다.")
    void persistEvent() {
        // given
        final PersistEventEntity persistEventEntity = new PersistEventEntity();
        final PersistEvent persistEvent = PersistEvent.of(persistEventEntity, eventSource);

        // when
        final PersistEventEntity persistedEntity = persistEventListener.onEvent(PersistEventEntity.class, persistEvent);

        // then
        final PersistEventEntity savedEntity = entityManager.find(PersistEventEntity.class, 1L);
        assertAll(
            () -> assertThat(savedEntity).isNotNull(),
            () -> {
                assert savedEntity != null;
                assertThat(persistedEntity.getId()).isEqualTo(savedEntity.getId());
            }
        );

    }

    @Entity
    static class PersistEventEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        public PersistEventEntity(Long id) {
            this.id = id;
        }

        protected PersistEventEntity() {
        }

        public Long getId() {
            return id;
        }
    }
}