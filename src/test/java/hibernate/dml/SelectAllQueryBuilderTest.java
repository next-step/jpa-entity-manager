package hibernate.dml;

import jakarta.persistence.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SelectAllQueryBuilderTest {

    private final SelectAllQueryBuilder selectAllQueryBuilder = SelectAllQueryBuilder.INSTANCE;

    @Test
    void select_all쿼리를_생성한다() {
        // given
        String expected = "select id, nick_name from test_entity;";

        // when
        String actual = selectAllQueryBuilder.generateQuery("test_entity", List.of("id", "nick_name"));

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Entity
    @Table(name = "test_entity")
    static class TestEntity {

        @Id
        private Long id;

        @Column(name = "nick_name")
        private String name;

        @Transient
        private String email;

        public TestEntity() {
        }

        public TestEntity(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }
    }
}
