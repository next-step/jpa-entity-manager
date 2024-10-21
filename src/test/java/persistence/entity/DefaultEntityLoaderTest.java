package persistence.entity;

import database.H2ConnectionFactory;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.dialect.Dialect;
import persistence.dialect.H2Dialect;
import persistence.fixture.EntityWithId;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.ddl.DropQueryBuilder;
import persistence.sql.dml.SelectQueryBuilder;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class DefaultEntityLoaderTest {
    private JdbcTemplate jdbcTemplate;
    private EntityPersister entityPersister;
    private Dialect dialect;
    private EntityWithId entity;

    @BeforeEach
    void setUp() {
        jdbcTemplate = new JdbcTemplate(H2ConnectionFactory.getConnection());
        entityPersister = new DefaultEntityPersister(jdbcTemplate);
        dialect = new H2Dialect();
        entity = new EntityWithId("Jaden", 30, "test@email.com", 1);

        createTable();
        insertData();
    }

    @AfterEach
    void tearDown() {
        dropTable();
    }

    @Test
    @DisplayName("엔티티를 조회한다.")
    void find() {
        // given
        final EntityLoader entityLoader = new DefaultEntityLoader(jdbcTemplate, new SelectQueryBuilder());

        // when
        final EntityWithId managedEntity = entityLoader.find(entity.getClass(), entity.getId());

        // then
        assertAll(
                () -> assertThat(managedEntity).isNotNull(),
                () -> assertThat(managedEntity.getId()).isEqualTo(entity.getId()),
                () -> assertThat(managedEntity.getName()).isEqualTo(entity.getName()),
                () -> assertThat(managedEntity.getAge()).isEqualTo(entity.getAge()),
                () -> assertThat(managedEntity.getEmail()).isEqualTo(entity.getEmail()),
                () -> assertThat(managedEntity.getIndex()).isNull()
        );
    }

    private void createTable() {
        final CreateQueryBuilder createQueryBuilder = new CreateQueryBuilder(entity.getClass(), dialect);
        jdbcTemplate.execute(createQueryBuilder.create());
    }

    private void insertData() {
        entityPersister.insert(entity);
    }

    private void dropTable() {
        final DropQueryBuilder dropQueryBuilder = new DropQueryBuilder(entity.getClass());
        jdbcTemplate.execute(dropQueryBuilder.drop());
    }
}
