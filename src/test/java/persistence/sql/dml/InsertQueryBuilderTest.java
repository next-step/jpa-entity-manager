package persistence.sql.dml;

import domain.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.metadata.EntityMetadata;
import persistence.sql.metadata.EntityValue;
import persistence.sql.metadata.EntityValues;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class InsertQueryBuilderTest {
	private final EntityMetadata entityMetadata = new EntityMetadata(Person.class);

	private final Person person = new Person("hhhhhwi", 1, "aab555586@gmail.com", 0);

	@DisplayName("Person 객체로 INSERT 쿼리 생성 테스트")
	@Test
	void test_buildQuery() {
		Field[] fields = person.getClass().getDeclaredFields();

		EntityValues entityValues = new EntityValues(Arrays.stream(fields)
				.map(x -> new EntityValue(x, person))
				.filter(EntityValue::checkPossibleToBeValue)
				.collect(Collectors.toList()));

		assertEquals(
				new InsertQueryBuilder().buildQuery(entityMetadata, entityValues),
				"INSERT INTO users (nick_name, old, email) VALUES ('hhhhhwi',1,'aab555586@gmail.com');"
		);
	}
}
