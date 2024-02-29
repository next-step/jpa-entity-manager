package persistence.entity;

public interface EntityPersister {

    boolean update(EntityMeta entity);

    Object insert(EntityMeta entity);

    void delete(EntityMeta entity);
}
