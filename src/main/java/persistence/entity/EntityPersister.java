package persistence.entity;

public interface EntityPersister {
    /*
    스냅샷을 만들 때 Object 가 아니라 EntityPersister 라는 인터페이스를 활용해 엔티티가 영속화 될 때
    데이터베이스로 부터 데이터를 pesister.getDatabaseSnapshot 메서드를 통해 가져옴
    너무 많은 로직이 있기에 간단하게 구현
     */
    Object getDatabaseSnapshot(Long id, Object entity);

    Object getCachedDatabaseSnapshot(Long id);

    Object load(Class<?> clazz, Long id);

    void insert(Object entity);

    void update(Object entity);

    void delete(Object entity);
}
