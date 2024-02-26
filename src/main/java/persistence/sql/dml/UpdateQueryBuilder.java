package persistence.sql.dml;

import persistence.sql.dialect.h2.H2Dialect;
import persistence.sql.dml.domain.Person;

public class UpdateQueryBuilder {
    public UpdateQueryBuilder(Class<Person> personClass, H2Dialect h2Dialect) {

    }

    public String createUpdateQuery(Person person) {
        return null;
    }
}
