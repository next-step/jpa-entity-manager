package persistence;

import builder.ddl.DDLBuilderData;
import builder.ddl.builder.CreateQueryBuilder;
import builder.ddl.builder.DropQueryBuilder;
import builder.ddl.dataType.DB;
import database.H2DBConnection;
import entity.Person;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


/*
- Persist로 Person 저장 후 영속성 컨텍스트에 존재하는지 확인한다.
- remove 실행하면 영속성 컨텍스트에 데이터가 제거된다.
- update 실행하면 영속성컨텍스트 데이터도 수정된다.
*/
class EntityLoaderTest {

    private EntityPersister entityPersister;
    private EntityLoader entityLoader;
    private H2DBConnection h2DBConnection;
    private JdbcTemplate jdbcTemplate;
    private PersistenceContext persistenceContext;

    @BeforeEach
    void setUp() throws SQLException {
        this.h2DBConnection = new H2DBConnection();
        this.jdbcTemplate = this.h2DBConnection.start();

        //테이블 생성
        CreateQueryBuilder queryBuilder = new CreateQueryBuilder();
        String createQuery = queryBuilder.buildQuery(DDLBuilderData.createDDLBuilderData(Person.class, DB.H2));

        jdbcTemplate.execute(createQuery);

        this.persistenceContext = new PersistenceContextImpl();

        this.entityPersister = new EntityPersister(jdbcTemplate);
        this.entityLoader = new EntityLoader(jdbcTemplate);
    }

    //정확한 테스트를 위해 메소드마다 테이블 DROP 후 DB종료
    @AfterEach
    void tearDown() {
        DropQueryBuilder queryBuilder = new DropQueryBuilder();
        String dropQuery = queryBuilder.buildQuery(DDLBuilderData.createDDLBuilderData(Person.class, DB.H2));
        jdbcTemplate.execute(dropQuery);
        this.h2DBConnection.stop();
    }

    @DisplayName("Persist로 Person 저장 후 영속성 컨텍스트에 존재하는지 확인한다.")
    @Test
    void findTest() {
        Person person = createPerson(1);
        this.entityPersister.persist(person);

        assertThat(this.entityLoader.find(Person.class, 1L))
                .extracting("id", "name", "age", "email")
                .contains(1L, "test1", 29, "test@test.com");
    }

    private Person createPerson(int i) {
        return new Person((long) i, "test" + i, 29, "test@test.com");
    }
}
