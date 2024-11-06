package persistence.defaulthibernate;


public enum EntryStatus {
    MANAGED, // 영속상태
    READ_ONLY, // 읽기전용 상태 데이터베이스에 반영되지 않으며 캐시만 사용
    DELETED, // 삭제예정상태
    GONE, // 영속성 컨텍스트에서 제거상태
    LOADING, // 데이터베이스에서 조회중인 상태
    SAVING // 영속성 컨텍스트에 저장되거나 추가되기 위한 과정 중에 있는 상태
    ;

    @Override
    public String toString() {
        return status();
    }

    private String status(){
        return switch (this) {
            case MANAGED -> "MANAGED";
            case READ_ONLY -> "READ_ONLY";
            case DELETED -> "DELETED";
            case GONE -> "GONE";
            case LOADING -> "LOADING";
            case SAVING -> "SAVING";
            default -> "";
        };
    }
}
