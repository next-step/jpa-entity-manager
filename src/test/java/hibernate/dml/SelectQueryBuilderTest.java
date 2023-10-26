package hibernate.dml;

import hibernate.entity.meta.column.EntityField;
import jakarta.persistence.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SelectQueryBuilderTest {

    private final SelectQueryBuilder selectQueryBuilder = SelectQueryBuilder.INSTANCE;

    @Test
    void select쿼리를_생성한다() throws NoSuchFieldException {
        // given
        String expected = "select id, nick_name from test_entity where id = 1;";

        // when
        String actual = selectQueryBuilder.generateQuery(
                "test_entity",
                List.of("id", "nick_name"),
                new EntityField(TestEntity.class.getDeclaredField("id")),
                1
        );

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
