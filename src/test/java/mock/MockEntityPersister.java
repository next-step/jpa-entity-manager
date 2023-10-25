package mock;

import persistence.entity.EntityPersister;

public class MockEntityPersister extends EntityPersister {
    public MockEntityPersister(final Class<?> clazz) {
        super(clazz, new MockPersistenceEnvironment());
    }

}
