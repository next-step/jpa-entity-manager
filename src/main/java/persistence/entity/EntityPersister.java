package persistence.entity;

/**
 * EntityPersister 는 엔터티의 메타데이터와 데이터베이스 매핑 정보를 제공하고,
 * 변경된 엔터티를 데이터베이스에 동기화하는 역할
 */
public interface EntityPersister {

    Object insert(Object entity);

    boolean update(Object entity);

    void delete(Object entity);
}
