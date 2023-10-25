package persistence.sql.dml.statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import database.DatabaseServer;
import database.H2;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.generator.CreateDDLQueryGenerator;
import persistence.sql.ddl.generator.DropDDLQueryGenerator;
import persistence.sql.dialect.H2ColumnType;
import persistence.sql.dml.Database;
import persistence.sql.dml.JdbcTemplate;
import persistence.sql.dml.clause.WherePredicate;
import persistence.sql.dml.clause.operator.EqualOperator;

@DisplayName("UPDATE 쿼리 생성 통합 테스트")
class UpdateStatementBuilderIntegrationTest {

    private DatabaseServer server;

    private Database jdbcTemplate;

    @BeforeEach
    void setUp() throws SQLException {
        server = new H2();
        server.start();

        jdbcTemplate = new JdbcTemplate(server.getConnection());
        CreateDDLQueryGenerator createDDLQueryGenerator = new CreateDDLQueryGenerator(new H2ColumnType());
        jdbcTemplate.execute(createDDLQueryGenerator.create(UpdateStateBuilderFixture.class));
    }

    @AfterEach
    void tearDown() {
        DropDDLQueryGenerator dropDDLQueryGenerator = new DropDDLQueryGenerator(new H2ColumnType());
        jdbcTemplate.execute(dropDDLQueryGenerator.drop(UpdateStateBuilderFixture.class));
        server.stop();
    }

    @Test
    @DisplayName("저장된 Entity의 값을 수정할 수 있다.")
    void canUpdateSavedEntityValue() throws SQLException {
        //given
        InsertStatementBuilder insertStatementBuilder = new InsertStatementBuilder(new H2ColumnType());
        final String originName = "james";
        final String mail = "james@email.com";
        final UpdateStateBuilderFixture james = new UpdateStateBuilderFixture(originName, mail);

        final String insertSql = insertStatementBuilder.insert(james);
        jdbcTemplate.execute(insertSql);

        //when
        final String updatedName = "updatedJames";
        final UpdateStateBuilderFixture updatedJames = new UpdateStateBuilderFixture(1L, updatedName, mail);
        final String updateSql = UpdateStatementBuilder.builder()
            .update(updatedJames, new H2ColumnType())
            .where(WherePredicate.of("id", 1, new EqualOperator()))
            .build();

        jdbcTemplate.execute(updateSql);

        //then
        Object idValue;
        Object nameValue;
        Object emailValue;

        final String selectSql = SelectStatementBuilder.builder()
            .select(james.getClass(), new H2ColumnType())
            .where(WherePredicate.of("id", 1, new EqualOperator()))
            .build();

        try (final ResultSet resultSet = jdbcTemplate.executeQuery(selectSql)) {
            resultSet.next();

            idValue = resultSet.getObject("ID");
            nameValue = resultSet.getObject("NAME");
            emailValue = resultSet.getObject("EMAIL");
        }

        assertAll(
            () -> assertThat(idValue).isEqualTo(1L),
            () -> assertThat(nameValue).isEqualTo(updatedName),
            () -> assertThat(emailValue).isEqualTo(mail)
        );
    }

    @Entity
    static class UpdateStateBuilderFixture {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;

        private String email;

        public UpdateStateBuilderFixture(String name, String email) {
            this.name = name;
            this.email = email;
        }

        protected UpdateStateBuilderFixture(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        protected UpdateStateBuilderFixture() {

        }
    }
}