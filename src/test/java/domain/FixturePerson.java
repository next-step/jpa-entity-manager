package domain;

public class FixturePerson {

    public static Person create(final Long id) {
        return new Person(id, "min", 30, "jongmin4943@gmail.com");
    }

}
