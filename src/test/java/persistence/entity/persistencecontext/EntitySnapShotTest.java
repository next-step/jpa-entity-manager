package persistence.entity.persistencecontext;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.Person;
import persistence.entity.PersonFixtures;
import persistence.entity.persistencecontext.EntitySnapShot;
import persistence.sql.entitymetadata.model.EntityColumn;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class EntitySnapShotTest {

    @DisplayName("Entity의 변경된 컬럼을 가져온다")
    @Test
    void getDirtyColumns() {
        // given
        Person person = PersonFixtures.fixture(1L, "name1", 0, "email1");
        EntitySnapShot entitySnapShot = EntitySnapShot.fromEntity(person);

        Person diffPerson = PersonFixtures.fixture(1L, "name2", 0, "email2");

        // when
        Set<EntityColumn<Person, ?>> dirtyColumns = entitySnapShot.getDirtyColumns(diffPerson);

        // then
        assertThat(dirtyColumns).hasSize(2);
        assertThat(dirtyColumns.stream().map(EntityColumn::getEntityFieldName)).contains("name", "email");
    }
}
