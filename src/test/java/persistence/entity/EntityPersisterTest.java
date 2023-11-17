package persistence.entity;

import jdbc.JdbcTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.Fixtures;

import static org.mockito.Mockito.*;

class EntityPersisterTest {

    @Test
    @DisplayName("update() 메서드 테스트 - 변경 사항이 없으면 update 쿼리를 만들지 않는다.")
    void update() {
        JdbcTemplate mockJdbcTemplate = mock(JdbcTemplate.class);
        EntityPersister entityPersister = new EntityPersister(mockJdbcTemplate);
        Person person = Fixtures.person1();

        entityPersister.update(person, person);

        verify(mockJdbcTemplate, never()).execute(anyString());
    }

}