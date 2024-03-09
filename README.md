# jpa-entity-manager

### 🚀 3단계 - First Level Cache, Dirty Check
- [x] 요구사항1 - PersistenceContext 구현체를 만들어 보고 1차 캐싱을 적용해보자
- [ ] 요구사항2 - snapshot 만들기
    1. 영속 컨텍스트 내에서 Entity 를 조회
    2. 조회된 상태의 Entity 를 스냅샷 생성
    3. 트랜잭션 커밋 후 해당 스냅샷과 현재 Entity 를 비교 (데이터베이스 커밋은 신경쓰지 않는다)
    4. 다른 점을 쿼리로 생성
- [ ] 요구사항3 - 더티체킹 구현
    - Snapshot 기반 Dirty Checking
    - 엔티티의 상태를 스냅샷으로 저장하여, 변경된 값이 있는지를 비교하는 방식입니다.
      1. 엔티티를 조회할 때 스냅샷을 만들어 둔 후, 
      2. 엔티티의 상태를 변경할 때마다 스냅샷과 비교하여 변경 내용을 감지합니다. 

