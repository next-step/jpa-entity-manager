# jpa-entity-manager

## Entity 구현

### step0

- [x] 이전 미션(jpa-query-builder)의 코드 옮기기

### step1

- [x] 엔터티의 데이터베이스 매핑, 쿼리 생성 및 실행의 책임을 EntityManger에서 EntityPersister로 옮겨주기

### step2

- [x] EntityPersister의 find에 대한 책임을 EntityLoader로 옮겨주기

### step3

- [x] PersistenceContext 구현체를 구현하고 1차 캐싱을 적용하기
  - Map을 필드로 가지는 캐시 일급 컬렉션을 만들자.
  - add 할 때는 map에 넣어준다.
  - remove 할 때는 map에서 제거한다.
  - 넣은 객체와 꺼낸 객체의 동일성을 보장하자.

- [x] 엔티티의 상태를 저장하는 snapshot 구현하기
  - 스냅샷을 저장하는 map을 만든다.
  - 엔티티를 조회했을 때 snapshot을 생성한다.
  - 엔티티를 merge할 때 해당 엔티티가 이미 존재하는 경우
    - 스냅샷을 확인한다.
    - 스냅샷이 존재하지 않으면 해당 엔티티를 데이터베이스에서 읽어온 후 비교한다.
