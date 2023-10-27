package hibernate.dml;

import hibernate.entity.meta.column.EntityColumn;
import hibernate.entity.meta.column.EntityField;
import jakarta.persistence.*;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static hibernate.dml.InsertQueryBuilder.INSTANCE;
import static org.assertj.core.api.Assertions.assertThat;

class InsertQueryBuilderTest {

    private final InsertQueryBuilder insertQueryBuilder = INSTANCE;

    @Test
    void insert쿼리를_생성한다() throws NoSuchFieldException {
        // given
        Map<EntityColumn, Object> fieldValues = new LinkedHashMap<>();
        fieldValues.put(new EntityField(TestEntity.class.getDeclaredField("id")), 1);
        fieldValues.put(new EntityField(TestEntity.class.getDeclaredField("name")), "최진영");
        String expected = "insert into test_entity (id, nick_name) values (1, '최진영');";

        // when
        String actual = insertQueryBuilder.generateQuery("test_entity", fieldValues);

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
