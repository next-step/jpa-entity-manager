package persistence.defaulthibernate;

import persistence.entity.EntityData;
import persistence.entity.EntityKey;

public interface PersistenceContext {

    void add(Object object, Long id);

    Object get(EntityKey entityKey);

    void update(EntityData eneityData, EntityKey entityKey);

    void remove(EntityKey entityKey) ;

}
