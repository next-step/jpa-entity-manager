package persistence.sql.dml;

import domain.Person;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.metadata.EntityMetadata;

import static org.junit.jupiter.api.Assertions.*;

class DeleteQueryBuilderTest {
	private final Person person = new Person(1L, "hhhhhwi", 1, "aab555586@gmail.com", 0);

	private final EntityMetadata entityMetadata = new EntityMetadata(person);

	@DisplayName("Person 객체로 DELETE 쿼리 생성 테스트")
	@Test
	void test_buildQuery() {
		Assertions.assertAll(
				() -> assertEquals(new DeleteQueryBuilder().buildQuery(entityMetadata),
						"DELETE FROM users;"),
				() -> assertEquals(new DeleteQueryBuilder().buildQuery(entityMetadata, new WhereClauseBuilder(entityMetadata)),
						"DELETE FROM users WHERE id = 1 AND nick_name = 'hhhhhwi' AND old = 1 AND email = 'aab555586@gmail.com';")
		);
	}

	@DisplayName("Person 객체로 PK 조건절이 있는 DELETE 쿼리 생성 테스트")
	@Test
	void test_buildByIdQuery() {
		assertEquals(
				new DeleteQueryBuilder().buildByIdQuery((entityMetadata)),
				"DELETE FROM users WHERE id = 1;"
		);
	}
}
