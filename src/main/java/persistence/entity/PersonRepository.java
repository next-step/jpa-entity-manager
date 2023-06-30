package persistence.entity;

public class PersonRepository extends CustomJpaRepository<Person, Long> {

    public PersonRepository(EntityManager entityManager) {
        super(entityManager);
    }
}
