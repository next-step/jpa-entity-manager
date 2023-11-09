package persistence;

import persistence.entity.Person;

public class Fixtures {

    public static Person person1() {
        return new Person("test1", 30, "test1@gmail.com");
    }

    public static Person person2() {
        return new Person("test2", 30, "test2@gmail.com");
    }

}
