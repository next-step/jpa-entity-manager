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
