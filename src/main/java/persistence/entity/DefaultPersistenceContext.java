package persistence.entity;

public class DefaultPersistenceContext implements PersistenceContext {
    @Override
    public Object getEntity(Long id) {
        return null;
    }

    @Override
    public void addEntity(Long id, Object entity) {

    }

    @Override
    public void removeEntity(Object entity) {

    }
}
