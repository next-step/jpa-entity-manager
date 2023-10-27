package persistence.sql.dml;

import domain.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.metadata.EntityMetadata;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdateQueryBuilderTest {
	private final UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();

	private final Person person = new Person(1L, "update", 2, "update@email.com", 0);

	private final EntityMetadata entityMetadata = new EntityMetadata(person);

	@DisplayName("Person 객체로 UPDATE 쿼리 생성 테스트")
	@Test
	void test_buildQuery() {
		assertEquals(updateQueryBuilder.buildQuery(entityMetadata, new WhereClauseBuilder(entityMetadata)),
				"UPDATE users SET nick_name = 'update', old = 2, email = 'update@email.com' WHERE id = 1 AND nick_name = 'update' AND old = 2 AND email = 'update@email.com';");
	}

	@DisplayName("Person 객체로 PK 조건절을 가진 UPDATE 쿼리 생성 테스트")
	@Test
	void test_buildByIdQuery() {
		assertEquals(updateQueryBuilder.buildByIdQuery(entityMetadata),
				"UPDATE users SET nick_name = 'update', old = 2, email = 'update@email.com' WHERE id = 1;");
	}
}