# jpa-entity-manager

## 1단계 - EntityPersister 구현
- [x] Persistence Context 구현
  - [x] Entity를 Map<식별자, 객체>으로 관리
  - [x] 1차 캐시 역할 구현
- [x] EntityPersister 구현
  - [x] Entity를 DB에 저장
  - [x] Entity를 DB에서 삭제
  - [x] Entity를 DB에서 수정

## 2단계 - EntityLoader 구현
- [x] RowMapper Reflection API 사용해 구현
- [x] EntityManager, Persistence Context 책임 전가

## 3단계 - Persistence Context, Dirty Check
- [x] 만들었던 PersistenceContext 에서 효율적인 메모리 관리를 위한 기능 구현 (1차 캐싱)
  - [ ] 더티체킹 구현
    - [x] 엔티티 로드 시 스냅샷 저장
      - [x] 스냅샷은 엔티티의 복사본으로 저장
      - [x] 이미 영속성 컨텍스트에서 조회되었던 엔티티는 스냅샷을 저장하지 않음
    - Hibernate 구현 참고 -> 
      - `persist()` 메서드는 `EntityManager`에 전달된 엔티티를 영속성 컨텍스트에 저장
      - `persist()` 메서드는 여러번 호출되어도 동일한 엔티티를 여러번 저장하지 않음 (1차 캐시에 영속 상태로 관리)
      - `detach()` 메서드는 영속성 컨텍스트에서 엔티티를 제거 (아마 추후에 상태값을 주어 관리해야 할 것으로 보임)
      - `persist()` 메서드는 준 영속상태의 엔티티를 전달받으면 예외를 발생 -> 이때는 merge를 통해 관리상태로 다시 바꿔야함
      - `merge()` 메서드는 준영속 상태의 엔티티(detached)를 영속 상태로 변경
    - [x] EntityManager.persist()
      - [x] Identity 전략일때
        - [x] id값이 null or 0인 경우 > 저장 후 id 값 바인딩
        - [x] id값이 null or 0이 아닌 entity가 넘어온 경우 -> exception
      - [x] Auto 전략일때
        - [x] id값이 null or 0인 경우 > Identifier of entity 'com.noah.hibernate.Team' must be manually assigned before calling 'persist()'
        - [x] id값이 null or 0이 아닌 entity가 넘어온 경우 -> 저장 됨
    - [x] EntityManager.update()
      - [x] 1차 캐시에 저장되어 관리되고 있는 Entity, Snapshot이 있는 지 확인
      - [x] Snapshot이 있다면, 1차 캐시에서 조회된 엔티티의 변경사항을 확인 후 update 쿼리 실행
      - [x] Snapshot이 없다면, 신규 저장되는 Entity이거나, 이미 영속성 컨텍스트에서 관리되고 있지 않은 엔티티
        - [x] 관리되지 않는 객체는 예외 처리
