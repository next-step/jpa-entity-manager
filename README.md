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
  - [ ] 엔티티 로드 시 스냅샷 저장
    - [ ] 스냅샷은 엔티티의 복사본으로 저장
    - [ ] 이미 영속성 컨텍스트에서 조회되었던 엔티티는 스냅샷을 저장하지 않음
  - [ ] 엔티티 수정 시 스냅샷과 비교
    - [ ] 수정된 엔티티와 스냅샷을 비교하여 변경된 필드 확인
    - [ ] 변경된 필드만 업데이트 쿼리 생성
