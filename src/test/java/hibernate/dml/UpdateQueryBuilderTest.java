package hibernate.dml;

import hibernate.entity.meta.column.EntityColumn;
import hibernate.entity.meta.column.EntityField;
import jakarta.persistence.*;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateQueryBuilderTest {

    private final UpdateQueryBuilder updateQueryBuilder = UpdateQueryBuilder.INSTANCE;

    @Test
    void update쿼리를_생성한다() throws NoSuchFieldException {
        // given
        Map<EntityColumn, Object> fieldValues = new LinkedHashMap<>();
        EntityField entityId = new EntityField(TestEntity.class.getDeclaredField("id"));
        fieldValues.put(new EntityField(InsertQueryBuilderTest.TestEntity.class.getDeclaredField("id")), 1);
        fieldValues.put(new EntityField(InsertQueryBuilderTest.TestEntity.class.getDeclaredField("name")), "최진영");
        String expected = "update test_entity set id = 1, nick_name = '최진영' where id = 1;";

        // when
        String actual = updateQueryBuilder.generateQuery("test_entity", fieldValues, entityId, 1);

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
