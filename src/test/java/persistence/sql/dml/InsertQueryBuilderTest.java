package persistence.sql.dml;

import domain.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.metadata.EntityMetadata;

import static org.junit.jupiter.api.Assertions.*;

class InsertQueryBuilderTest {
	private final Person person = new Person("hhhhhwi", 1, "aab555586@gmail.com", 0);

	private final EntityMetadata entityMetadata = new EntityMetadata(person);

	@DisplayName("Person 객체로 INSERT 쿼리 생성 테스트")
	@Test
	void test_buildQuery() {
		assertEquals(
				new InsertQueryBuilder().buildQuery(entityMetadata),
				"INSERT INTO users (nick_name, old, email) VALUES ('hhhhhwi',1,'aab555586@gmail.com');"
		);
	}
}
