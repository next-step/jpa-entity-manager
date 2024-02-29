# jpa-entity-manager

## 1단계 엔터티 매핑 (EntityPersister)

1. `EntityPersister` 클래스를 구현한다. 
   1. [x] update method를 구현한다. (파라미터 제한없이)
   2. [x] insert method를 구현한다. (파라미터 제한없이)
   3. [x] delete method를 구현한다. (파라미터 제한없이)

2. `EntityManager`에서 쿼리 생성과 데이터 매핑에 관한 책임은 `EntityPersister`로 이동한다.

## 2단계 - 엔터티 초기화 (EntityLoader)

1. `RowMapper`를 리팩터링하여 동적으로 `Entity`를 생성한다.
   - 구현 클래스 명은 `EntityLoader`로 Read 작업을 수행해 `Entity`를 로드하는 역할을 하도록 한다.
2. `EntityManager`에서 Read에 대한 책임은 `EntityLoader`로 이동한다.
