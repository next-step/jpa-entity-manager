package persistence.fixture;

import domain.Person;

public class PersonFixture {

    public static Person createPerson() {
        return Person.of("user1", 1, "abc@gtest.com", 0);
    }
}
