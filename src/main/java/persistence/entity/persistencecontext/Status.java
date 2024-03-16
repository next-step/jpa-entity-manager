package persistence.entity.persistencecontext;

/**
 * 영속성 컨텍스트 내 entity의 상태값
 *
 * - MANAGED : 엔티티가 영속성 컨텍스트 내에서 관리되고 있는 상태. 즉, 영속성 컨텍스트에 저장되어 있는 엔티티
 * - READ_ONLY : 엔티티가 읽기 전용 상태. 이 상태의 엔티티는 수정이 불가능하며, 영속성 컨텍스트를 통해 읽기만 가능하다.
 * - DELETED : 엔티티가 삭제된 상태. 이 상태의 엔티티는 영속성 컨텍스트에서 삭제되었으나, 아직 데이터베이스에서 완전히 삭제되지 않았을 수 있다.
 * - GONE: 엔티티가 영구적으로 삭제된 상태. 즉, 데이터베이스에서 완전히 삭제된 엔티티를 나타낸다.
 * - LOADING: 엔티티가 로딩 중인 상태. 이 상태의 엔티티는 영속성 컨텍스트에서 관리되며 데이터베이스에서 로딩 중이다.
 * - SAVING: 엔티티가 저장중인 상태. 이 상태의 엔티티는 영속성 컨텍스트에서 관리되며, 데이터 베이스에 저장되는 중이다.
 */
public enum Status {
    MANAGED,
    READ_ONLY,
    DELETED,
    GONE,
    LOADING,
    SAVING
}
