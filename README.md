# jpa-entity-manager

### step1 - 엔터티 매핑 (EntityPersister)

- [x] 요구사항 1 - 엔터티의 데이터베이스 매핑, 쿼리 생성 및 실행
- [x] 요구사항 2 - EntityManager 의 책임 줄여주기
  - EntityManager 의 구현체에서 쿼리 생성 및 데이터 매핑 에 대한 책임을 EntityPersister 로 옮겨주자

### step2 - 엔터티 초기화 (EntityLoader)

- [x] 요구 사항 1 - RowMapper 리팩터링
  - EntityLoader라는 클래스를 만들어 구현
  - EntityLoader는 데이터베이스 쿼리를 실행하여 엔티티 객체를 로드하는 역할
- [x] 요구 사항 2 - EntityManager 의 책임 줄여주기
  - EntityManager 의 구현체에서 find 에 대한 책임을 EntityLoader 로 옮기기


### step3 - First Level Cache, Dirty Check

- [x] 요구 사항 1 - PersistenceContext 구현체를 만들어 보고 1차 캐싱 적용
- [x] 요구 사항 2 - snapshot 만들기
  - Snapshot은 영속성 컨텍스트 내부의 데이터를 복사한 것
    1. 영속 컨텍스트 내에서 Entity 를 조회
    2. 조회된 상태의 Entity 를 스냅샷 생성
    3. 트랜잭션 커밋 후 해당 스냅샷과 현재 Entity 를 비교 (데이터베이스 커밋은 신경쓰지 않는다)
    4. 다른 점을 쿼리로 생성

- [ ] 요구 사항 3 - dirty check 구현하기
  - 엔티티의 상태를 스냅샷으로 저장하여, 변경된 값이 있는지를 비교하는 방식
  1. 엔티티를 조회할 때 스냅샷을 만들어 둔 후,
  2. 엔티티의 상태를 변경할 때마다 스냅샷과 비교하여 변경 내용을 감지합니다.
