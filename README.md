# jpa-entity-manager


## 0단계 - 기본 코드 준비

- [X] 이전 미션 코드 가져오기

--- 
## 1단계 엔티티 맵핑 (EntityPersister)
> EntityPersister는 엔터티의 메타데이터와 데이터베이스 매핑 정보를 제공하고, 변경된 엔터티를 데이터베이스에 동기화하는 역할

- [X] 요구사항 1 - 엔터티의 데이터베이스 매핑, 쿼리 생성 및 실행
  - [X] EntityPersister로 쿼리 맵핑 
    - [X] insert
    - [X] update
    - [X] delete
  - [X] EntityPersister로 쿼리 실행
- [X] 요구사항 2 - EntityManager 의 책임 줄여주기
  - [X] EntityManager 의 구현체에서 쿼리 생성 및 데이터 매핑 에 대한 책임을 EntityPersister 로 옮겨주자

## 2단계 엔터티 초기화 (EntityLoader)
> EntityLoader 는 엔터티를 데이터베이스에서 로드하고 로드된 엔터티 상태를 영속성 컨텍스트 내에서 추적 및 관리

- [X] 요구사항 1 - RowMapper 리팩터링
  -  [X] 동적으로 생성하는 엔터티로더(`EntityLoader`) 생성
- [X] 요구사항 2 - EntityManager 의 책임 줄여주기

---
# 3단계 - First Level Cache, Dirty Check 구현 
 - [X] 요구사항1 - PersistenceContext 구현체를 만들어 보고 1차 캐싱을 적용해보자
 - [X] 요구사항2 - snapshot 만들기
   - [X] snapshot 객체 만들기 
   - [X] snapshot을 저장할 수 있는 공간 구현 
   - [X] 변경된 엔터티 조회
   - [X] 원본과 스냅샷으로 수정된 쿼리 만들기 
 - [X] 요구사항3 - Dirty Check 기능을 구현해보자
   - [X] 클래스로더 만들기 
   - [X] 엔티티매니저 팩토리를 만들고 캐싱하기

# 4단계 - EntityEntry 구현
> EntityEntry 클래스는 엔터티의 영속성 상태와 상태 변화, 생명주기와 변경감지에 중요한 역할

- [X] SAVING 상태 이해하기
  - SAVING 상태는 save, persist 메서드가 호출되어 데이터 베이스에 저장하기 시작했을때 를 의미
  - 아직 DB에는 저장되지 않은 상태 
  - 저장 되면 MANAGED 상태로 변경된다.
- [X] SAVING에 대응 되게 구현  


- [X] MANAGED 상태 이해하기
    - [X] 영속성 컨텍스트 내에서 관리되고 있는 상태
- [X] MANAGED 구현하기
- 
- [ ] DETACHED 상태 이해하기
- [ ] REMOVED 상태 이해하기
- [ ] PERSISTENT 상태 이해하기
 





