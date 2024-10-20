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
- [ ] EntityManager, Persistence Context 책임 전가
