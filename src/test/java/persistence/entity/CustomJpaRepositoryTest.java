package persistence.entity;

import database.DatabaseServer;
import database.H2;
import domain.Person;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.H2DdlQueryBuilder;
import persistence.sql.metadata.EntityMetadata;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class CustomJpaRepositoryTest {
	public static PersistenceContext persistenceContext;

	public static EntityManager entityManager;

	public static CustomJpaRepository<Person> customJpaRepository;

	@BeforeAll
	static void setJdbcTemplate() throws SQLException {
		DatabaseServer server = new H2();
		server.start();

		JdbcTemplate jdbcTemplate = new JdbcTemplate(server.getConnection());

		EntityMetadata entityMetadata = new EntityMetadata(new Person());

		jdbcTemplate.execute(H2DdlQueryBuilder.build().dropQuery(entityMetadata));
		jdbcTemplate.execute(H2DdlQueryBuilder.build().createQuery(entityMetadata));

		persistenceContext = new SimplePersistenceContext();
		entityManager = new SimpleEntityManager(new EntityPersister(jdbcTemplate), new EntityLoader(jdbcTemplate), persistenceContext);
		customJpaRepository = new CustomJpaRepository<>(entityManager);
	}

	@DisplayName("엔티티가 저장이 되면 영속성 컨텍스트에 저장된다.")
	@Test
	void When_EntitySave_Then_EntityIsPersistent() {
		customJpaRepository.save(new Person("name", 1, "email@gmail.com", 0));
		Person resultPerson = persistenceContext.getEntity(Person.class, 1L);

		assertEquals(new Person(1L, "name", 1, "email@gmail.com", 0), resultPerson);
	}

	@DisplayName("엔티티가 영속성 컨텍스트에 존재할 경우 Update 한다.")
	@Test
	void When_EntityIsAlreadyPersistent_Then_Update() {
		persistenceContext.addEntity(2L, new Person("name", 1, "email@gmail.com", 0));

		customJpaRepository.save(new Person(2L, "hhhhhwi", 10, "aab555586@gmail.com", 0));

		Person resultPerson = entityManager.find(Person.class, 2L);

		assertEquals(new Person(2L, "hhhhhwi", 10, "aab555586@gmail.com", 0), resultPerson);
	}
}
