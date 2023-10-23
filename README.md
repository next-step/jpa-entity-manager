# jpa-entity-manager


## 0단계 - 기본 코드 준비

- [X] 이전 미션 코드 가져오기

--- 
## 1단계 엔티티 맵핑 (EntityPersister)
> EntityPersister는 엔터티의 메타데이터와 데이터베이스 매핑 정보를 제공하고, 변경된 엔터티를 데이터베이스에 동기화하는 역할

- [ ] 요구사항 1 - 엔터티의 데이터베이스 매핑, 쿼리 생성 및 실행
  - [ ] EntityPersister로 쿼리 맵핑 
    - [X] insert
    - [X] update
    - [X] delete
  - [ ] EntityPersister로 쿼리 실행
- [ ] 요구사항 2 - EntityManager 의 책임 줄여주기
  - [ ] EntityManager 의 구현체에서 쿼리 생성 및 데이터 매핑 에 대한 책임을 EntityPersister 로 옮겨주자

