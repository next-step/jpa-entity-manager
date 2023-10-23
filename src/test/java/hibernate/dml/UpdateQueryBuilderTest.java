package hibernate.dml;

import hibernate.entity.EntityClass;
import jakarta.persistence.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateQueryBuilderTest {

    private final UpdateQueryBuilder updateQueryBuilder = UpdateQueryBuilder.INSTANCE;

    @Test
    void update쿼리를_생성한다() {
        // given
        TestEntity givenEntity = new TestEntity(1L, "최진영", "jinyoungchoi95@gmail.com");
        String expected = "update test_entity set id = 1, nick_name = '최진영' where id = 1;";

        // when
        String actual = updateQueryBuilder.generateQuery(new EntityClass<>(TestEntity.class), givenEntity);

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
