package persistence.entity;

import java.util.List;

public interface EntityLoader {

    /**
     * Selects an entity by its id
     * @param entityClass the entity class
     * @param id the id of the entity
     * @return the entity
     * @param <T> the entity type
     */
    <T> T select(Class<T> entityClass, Object id);

    /**
     * Selects all entities of the given class
     * @param entityClass the entity class
     * @return the list of entities
     * @param <T> the entity type
     */
    <T> List<T> selectAll(Class<T> entityClass);

    /**
     * Checks if an entity exists by its id
     * @param entityClass the entity class
     * @param id the id of the entity
     * @return true if the entity exists, false otherwise
     */
    boolean isExists(Class<?> entityClass, Object id);
}
