package persistence.sql.dml;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.fixture.EntityWithId;
import persistence.sql.meta.EntityTable;

import static org.assertj.core.api.Assertions.*;

class UpdateQueryBuilderTest {
    @Test
    @DisplayName("update 쿼리를 생성한다.")
    void update() {
        // given
        final UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();
        final EntityWithId entity = new EntityWithId(1L, "Jackson", 20, "test@email.com");
        final EntityTable entityTable = new EntityTable(entity);

        // when
        final String query = updateQueryBuilder.update(entity, entityTable.getEntityColumns());

        // then
        assertThat(query).isEqualTo("UPDATE users SET id = 1, nick_name = 'Jackson', old = 20, email = 'test@email.com' WHERE id = 1");
    }
}
