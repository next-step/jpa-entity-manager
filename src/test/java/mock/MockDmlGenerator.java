package mock;

import persistence.dialect.h2.H2Dialect;
import persistence.sql.dml.DmlGenerator;

public class MockDmlGenerator extends DmlGenerator {
    public MockDmlGenerator() {
        super(new H2Dialect());
    }
}
