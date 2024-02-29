# jpa-entity-manager

## 1단계 엔터티 매핑 (EntityPersister)

1. `EntityPersister` 클래스를 구현한다. 
   1. [x] update method를 구현한다. (파라미터 제한없이)
   2. [x] insert method를 구현한다. (파라미터 제한없이)
   3. [x] delete method를 구현한다. (파라미터 제한없이)

2. `EntityManager`에서 쿼리 생성과 데이터 매핑에 관한 책임은 `EntityPersister`로 이동한다.
