package persistence.entity;

public class PersistenceContextImpl implements PersistenceContext {
    @Override
    public <T> T getEntity(Class<T> entityClass, Long id) {
        return null;
    }

    @Override
    public void addEntity(Object entityObject) {

    }

    @Override
    public void removeEntity(Object entityObject) {

    }
}
