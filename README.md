# jpa-entity-manager

## Entity 구현


### 1단계 - 엔터티 매핑 (EntityPersister)
`EntityPersister는 엔터티의 메타데이터와 데이터베이스 매핑 정보를 제공하고, 변경된 엔터티를 데이터베이스에 동기화하는 역할`
1. 엔터티 클래스와 데이터베이스 테이블 간의 매핑 및 데이터베이스 작업을 처리합니다.
2. 엔터티의 상태를 데이터베이스와 동기화시키고 CRUD(Create, Read, Update, Delete) 작업을 수행하는 주체입니다.
3. 엔터티 캐싱을 지원하고, 반복적인 데이터베이스 조회를 최적화하는 데 사용됩니다. (우리는 캐싱이 항상 true 로 가정)

- 요구사항 1 - 엔터티의 데이터베이스 매핑, 쿼리 생성 및 실행
```java
public class EntityPersister {
- 구현 해보기

public boolean update(parameters는 자유롭게)

public void insert(parameters는 자유롭게)

public void delete(parameters는 자유롭게)
...
}
```
- [x] 엔터티 클래스와 DB 테이블 간의 매핑을 담당하게 한다.
- [x] 실제 DB 와 CURD 작업을 한다.
- [x] 반복적인 쿼리를 캐싱한다.

- 요구사항 2 - EntityManager 의 책임 줄여주기

`EntityManager 의 구현체에서 쿼리 생성 및 데이터 매핑 에 대한 책임을 EntityPersister 로 옮겨주자`

- [x] 기존 SimpleEntityManager 가 하던 find, persist, remove 로직을 EntityPersister 에게 위임한다.
