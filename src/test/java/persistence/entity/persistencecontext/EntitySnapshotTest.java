package persistence.entity.persistencecontext;

import domain.Person;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.fixture.PersonFixture;

@DisplayName("EntitySnapshot class는")
class EntitySnapshotTest {

    @DisplayName("같은 객체로 생성된 스냅샷은 같은 hash를 가진다.")
    @Test
    void testSameSnapshotHash() {
        // given
        Person person = PersonFixture.createPerson();
        EntitySnapshot snapshot = EntitySnapshot.from(person);
        EntitySnapshot snapshot1 = EntitySnapshot.from(person);

        // when
        int hash = snapshot.hashCode();
        int hash1 = snapshot1.hashCode();

        // then
        assertEquals(hash, hash1);
        assertEquals(snapshot, snapshot1);
    }

    @DisplayName("필드가 업데이트 되면 스냅샷의 hash도 변경된다.")
    @Test
    void testUpdateSnapshotHash() {
        // given
        Person person = PersonFixture.createPerson();
        EntitySnapshot snapshot = EntitySnapshot.from(person);

        // when
        person.updateName("user2");
        EntitySnapshot snapshot1 = EntitySnapshot.from(person);

        // then
        assertNotEquals(snapshot.hashCode(), snapshot1.hashCode());
    }
}
