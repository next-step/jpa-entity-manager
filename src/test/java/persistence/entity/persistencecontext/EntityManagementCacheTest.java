package persistence.entity.persistencecontext;

import jdbc.JdbcTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import jdbc.FakeJdbcTemplate;
import persistence.entity.Person;
import persistence.entity.entitymanager.EntityLoader;
import persistence.entity.entitymanager.EntityManagementCache;
import persistence.entity.entitymanager.EntityPersister;
import persistence.sql.dbms.Dialect;

import static org.assertj.core.api.Assertions.*;

class EntityManagementCacheTest {
    private JdbcTemplate jdbcTemplate;
    private EntityManagementCache entityManagementCache;

    @BeforeEach
    void setUp() {
        jdbcTemplate = new FakeJdbcTemplate();
        entityManagementCache = new EntityManagementCache(jdbcTemplate, Dialect.H2);
    }

    @DisplayName("persister는 entityClass에 해당하는 EntityPersister를 반환하고 한번 생성된 EntityPersister는 캐싱한다")
    @Test
    void persister() {
        EntityPersister<Person> entityPersister = entityManagementCache.persister(Person.class);
        EntityPersister<Person> entityPersister2 = entityManagementCache.persister(Person.class);
        EntityPersister<Person> entityPersister3 = entityManagementCache.persister(Person.class);

        assertThat(entityPersister).isSameAs(entityPersister2).isSameAs(entityPersister3);
    }

    @DisplayName("loader는 entityClass에 해당하는 EntityLoader 반환하고 한번 생성된 EntityLoader 캐싱한다")
    @Test
    void loader() {
        EntityLoader<Person> entityLoader = entityManagementCache.loader(Person.class);
        EntityLoader<Person> entityLoader2 = entityManagementCache.loader(Person.class);
        EntityLoader<Person> entityLoader3 = entityManagementCache.loader(Person.class);

        assertThat(entityLoader).isSameAs(entityLoader2).isSameAs(entityLoader3);
    }
}
