package persistence;


import builder.dml.DMLBuilderData;

public interface EntityManager {

    <T> T find(Class<T> clazz, Long id);

    void persist(Object entityInstance);

    void merge(Object entityInstance);

    void remove(Object entityInstance);

    DMLBuilderData checkDirtyCheck(Object entityInstance);

}
