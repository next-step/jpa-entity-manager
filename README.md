# jpa-entity-manager

## Entity 구현

### step0

- [x] 이전 미션(jpa-query-builder)의 코드 옮기기

### step1

- [x] 엔터티의 데이터베이스 매핑, 쿼리 생성 및 실행의 책임을 EntityManger에서 EntityPersister로 옮겨주기

### step2

- [x] EntityPersister의 find에 대한 책임을 EntityLoader로 옮겨주기

### step3

- [ ] PersistenceContext 구현체를 구현하고 1차 캐싱을 적용하기
  - Map을 필드로 가지는 캐시 일급 컬렉션을 만들자.
  - add 할 때는 map에 넣어준다.
  - remove 할 때는 map에서 제거한다.
  - 넣은 객체와 꺼낸 객체의 동일성을 보장하자.
