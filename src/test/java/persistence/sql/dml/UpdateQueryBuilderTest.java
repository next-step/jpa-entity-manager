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

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdateQueryBuilderTest {
	private final UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();

	private final EntityMetadata entityMetadata = new EntityMetadata(Person.class);

	private final Person person = new Person(1L, "update", 2, "update@email.com", 0);

	@DisplayName("Person 객체로 UPDATE 쿼리 생성 테스트")
	@Test
	void test_buildQuery() {
		Field[] fields = person.getClass().getDeclaredFields();

		EntityValues entityValues = new EntityValues(Arrays.stream(fields)
				.map(x -> new EntityValue(x, person))
				.filter(EntityValue::checkPossibleToBeValue)
				.collect(Collectors.toList()));

		assertEquals(updateQueryBuilder.buildByIdQuery(entityMetadata, entityValues, new WhereClauseBuilder(person)), "UPDATE users SET nick_name = 'update', old = 2, email = 'update@email.com' WHERE id = 1;");
	}
}