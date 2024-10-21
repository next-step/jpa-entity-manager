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
    - [x] 엔티티를 영속성 컨텍스트에 저장
    - [x] 스냅샷 저장
    - [x] Database에 insert 쿼리 실행 (현재는 flush 개념 구현 전이므로 insert 쿼리는 바로 실행)
      - [x] 만약 영속성 컨텍스트에 관리되고 있던 Entity가 다시 persist요청이 오게 되면
        - [x] 그냥 반환. (이미 영속성 컨텍스트에 관리되고 있기 때문... => 추가 insert쿼리 실행하지 않음)
  - [ ] EntityManager.update()
    - [ ] 1차 캐시에 저장되어 관리되고 있는 Entity, Snapshot이 있는 지 확인
    - [ ] Snapshot이 있다면, 1차 캐시에서 조회된 엔티티의 변경사항을 확인 후 update 쿼리 실행
    - [ ] Snapshot이 없다면, Database에서 조회
      - [ ] Database에서 조회한 값이 없다면 -> insert 쿼리 실행
        - Hibernate에서 `merge()`메서드가 새로 생긴 엔티티를 넘기면 insert만 실행. 
          - 생각해보면 db에 값이 없고 전달받은 Entity가 새로운 Entity라면 insert만 실행해야 맞는 것 같음.
      - [ ] Database에서 조회한 값이 있다면 -> 
        - 전달 받은 엔티티를 영속성 컨텍스트에 저장
        - 스냅샷과 비교 후 변경사항이 있다면 update 쿼리 실행 
        - [ ] update 쿼리 실행 후 1차 캐시 및 Snapshot 업데이트
