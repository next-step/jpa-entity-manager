package persistence.entity;

public enum EntityStatus {
    MANAGED,    // 영속 상태
    READ_ONLY,  // 읽기 전용 상태
    DELETED,    // 삭제 예정 상태
    GONE,       // 영속성 컨텍스트에서 제거된 상태
    LOADING,    // 로딩 중 상태
    SAVING      // 저장 중 상태
}
