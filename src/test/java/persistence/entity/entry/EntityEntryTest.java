package persistence.entity.entry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static persistence.TestUtils.createDataDefinitionLanguageAssembler;
import static persistence.TestUtils.createDataManipulationLanguageAssembler;

import database.DatabaseServer;
import database.H2;
import java.sql.SQLException;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.Person;
import persistence.entity.context.PersistenceContext;
import persistence.entity.context.PersistenceContextImpl;
import persistence.entity.loader.EntityLoader;
import persistence.entity.persister.EntityPersister;
import persistence.sql.ddl.assembler.DataDefinitionLanguageAssembler;
import persistence.sql.dml.assembler.DataManipulationLanguageAssembler;
import persistence.sql.usecase.GetFieldFromClass;
import persistence.sql.usecase.SetFieldValue;

class EntityEntryTest {
    private final DataManipulationLanguageAssembler dataManipulationLanguageAssembler = createDataManipulationLanguageAssembler();
    private final DataDefinitionLanguageAssembler dataDefinitionLanguageAssembler = createDataDefinitionLanguageAssembler();

    private DatabaseServer server;
    private JdbcTemplate jdbcTemplate;
    private PersistenceContext persistenceContext;
    private EntityPersister entityPersister;
    private EntityLoader entityLoader;
    private EntityEntry entityEntry;

    @BeforeEach
    void setUp() throws SQLException {
        server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        persistenceContext = new PersistenceContextImpl();
        entityPersister = new EntityPersister(dataManipulationLanguageAssembler, jdbcTemplate);
        entityLoader = new EntityLoader(new GetFieldFromClass(), new SetFieldValue(), jdbcTemplate, dataManipulationLanguageAssembler);
        entityEntry = new EntityEntry(entityPersister, entityLoader, persistenceContext);
        jdbcTemplate.execute(dataDefinitionLanguageAssembler.assembleCreateTableQuery(Person.class));
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(dataDefinitionLanguageAssembler.assembleDropTableQuery(Person.class));
        server.stop();
    }

    @Test
    @DisplayName("업데이트 후 조회하면 업데이트 된 객체가 반환한다.")
    void test_update_find() {
        // given
        Person p = new Person("hello", 21, "hello@naver.com");
        Long id = entityEntry.insert(p);
        p.setId(id);

        // when
        p.updateEmail("test@naver.com");
        entityEntry.update(p, id);

        // then
        Person findPerson = entityEntry.find(Person.class, id);
        assertAll(
            () -> assertThat(p == findPerson).isTrue(),
            () -> assertThat(findPerson.getEmail()).isEqualTo("test@naver.com")
        );
    }

    @Test
    @DisplayName("삭제 후 조회하면 null 반환한다.")
    void test_delete_find() {
        // given
        Person p = new Person("hello", 21, "hello@naver.com");
        Long id = entityEntry.insert(p);
        p.setId(id);

        // when
        entityEntry.delete(p);

        // then
        Person findPerson = entityEntry.find(Person.class, id);
        assertAll(
            () -> assertThat(findPerson).isNull()
        );
    }
}
