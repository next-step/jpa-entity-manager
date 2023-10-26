package mock;

import persistence.core.PersistenceEnvironment;
import persistence.dialect.h2.H2Dialect;

public class MockPersistenceEnvironment extends PersistenceEnvironment {

    public MockPersistenceEnvironment() {
        super(new MockDatabaseServer(), new H2Dialect());
    }
}
