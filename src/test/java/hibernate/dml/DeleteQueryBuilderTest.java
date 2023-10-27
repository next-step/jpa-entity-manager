package hibernate.dml;

import hibernate.entity.meta.column.EntityField;
import jakarta.persistence.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DeleteQueryBuilderTest {

    private final DeleteQueryBuilder deleteQueryBuilder = DeleteQueryBuilder.INSTANCE;

    @Test
    void delete쿼리를_생성한다() throws NoSuchFieldException {
        // given
        String expected = "delete from test_entity where id = 1;";

        // when
        String actual = deleteQueryBuilder.generateQuery("test_entity", new EntityField(TestEntity.class.getDeclaredField("id")), 1);

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
