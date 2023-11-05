package persistence.entity;

import domain.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SnapshotTest {

	@Test
	@DisplayName("스냅샷 생성 후 객체를 변경하면 변경된 필드가 존재한다.")
	void When_CreateSnapshot_And_UpdateEntity_Then_ChangedColumnsExist() {
		Person person = new Person("name", 1, "email@email.com", 0);
		Snapshot snapshot = new Snapshot(person);

		person.setName("hhhhhwi");
		person.setEmail("aab555586@gmail.com");

		assertEquals(snapshot.getChangedColumns(person).length, 2);
	}
}
