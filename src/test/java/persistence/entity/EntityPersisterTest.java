package persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EntityPersisterTest {

    @Entity
    private static class QueryTestEntity {
        @Id
        private Long id;

        @Column(name = "nick_name", length = 60)
        private String name;

        private Integer age;

        public QueryTestEntity() {
        }

        public QueryTestEntity(Long id) {
            this.id = id;
        }

        public QueryTestEntity(Long id, String name, Integer age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }
    }

    @Test
    void shouldCreateInsertQuery() {
        QueryTestEntity entity = new QueryTestEntity(1L, "John", 25);
        EntityPersister persister = new EntityPersister(QueryTestEntity.class);

        String query = persister.buildInsertQuery(entity);
        assertThat(query).isEqualTo("INSERT INTO QueryTestEntity (id, nick_name, age) VALUES (1, 'John', 25);");
    }

    @Test
    void shouldCreateInsertQueryWithNullValue() {
        QueryTestEntity entity = new QueryTestEntity(1L);
        EntityPersister persister = new EntityPersister(QueryTestEntity.class);

        String query = persister.buildInsertQuery(entity);
        assertThat(query).isEqualTo("INSERT INTO QueryTestEntity (id) VALUES (1);");
    }

    @Test
    void shouldCreateUpdateQuery() {
        QueryTestEntity entity = new QueryTestEntity(1L, "John", 25);
        EntityPersister persister = new EntityPersister(QueryTestEntity.class);

        String query = persister.buildUpdateQuery(entity);
        assertThat(query).isEqualTo("UPDATE QueryTestEntity SET nick_name = 'John', age = 25 WHERE id = 1;");
    }

    @Test
    void shouldCreateDeleteQuery() {
        QueryTestEntity entity = new QueryTestEntity(1L);
        EntityPersister persister = new EntityPersister(QueryTestEntity.class);

        String query = persister.buildDeleteQuery(entity);
        assertThat(query).isEqualTo("DELETE FROM QueryTestEntity WHERE id = 1;");
    }
}
