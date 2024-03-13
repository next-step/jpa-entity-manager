package persistence;

import persistence.sql.ddl.PersonV3;

public class PersonV3FixtureFactory {

    public static PersonV3 generatePersonV3Stub(final Long id) {
        final String name = "name";
        final int age = 20;
        final String email = "email@domain.com";
        return new PersonV3(id, name, age, email, 1);
    }

    public static PersonV3 generatePersonV3Stub() {
        return generatePersonV3Stub(0L);
    }

}
