package persistence.entity;

import persistence.sql.ddl.Person;

public class PersonJpaRepository extends CustomJpaRepository<Person, Long> {

    public PersonJpaRepository(EntityManager entityManager) {
        super(entityManager);
    }
}
