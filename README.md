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
  - [ ] EntityManager.persist()
    - [ ] 엔티티를 영속성 컨텍스트에 저장
    - [ ] 스냅샷 저장
    - [ ] Database에 insert 쿼리 실행 (현재는 flush 개념 구현 전이므로)
      - [ ] 만약 영속성 컨텍스트에 관리되고 있던 Entity가 다시 persist요청이 오게 되면
        - [ ] update 실행. 
  - [ ] EntityManager.update()
    - [ ] 1차 캐시에 저장되어 관리되고 있는 Snapshot이 있는 지 확인
    - [ ] Snapshot이 있다면, 엔티티의 변경사항을 확인 후 update 쿼리 실행
    - [ ] Snapshot이 없다면, Database에서 조회 후 Snapshot 저장
    - [ ] 저장 후 변경 감지 및 update 쿼리 실행
      - [ ] update 쿼리 실행 후 1차 캐시 및 Snapshot 업데이트
