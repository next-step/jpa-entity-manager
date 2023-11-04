package persistence.entity;

import domain.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimplePersistenceContextTest {
	private SimplePersistenceContext persistenceContext = new SimplePersistenceContext();

	@DisplayName("엔티티가 영속됐을 경우, 엔티티의 상태값은 MANAGED 이다.")
	@Test
	void When_EntityPersistent_Then_EntityStatusIsManaged() {
		persistenceContext.addEntity(1L, new Person("name", 1, "email@gmail.com", 0));

		assertEquals(persistenceContext.getStatus(Person.class, 1L), EntityStatus.MANAGED);
	}
}
