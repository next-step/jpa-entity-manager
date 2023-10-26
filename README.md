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


### 2단계 - 엔터티 초기화 (EntityLoader)
`EntityLoader 는 엔터티를 데이터베이스에서 로드하고 로드된 엔터티 상태를 영속성 컨텍스트 내에서 추적 및 관리`

- 요구사항 1 - RowMapper 리팩터링
- [x] RowMapper 를 클래스로 분리해서 책임을 맡긴다.
- [x] RowMapper 클래스는 변환할 엔터티의 정보를 미리 가지고 있다가 객체를 받아 처리한다.

- 요구사항 2 - EntityManager 의 책임 줄여주기
- [x] 기존 SimpleEntityManager 가 하던 find 로직을 EntityLoader 에게 위임한다.


### 3단계 - First Level Cache, Dirty Check
- 요구사항 1 - PersistenceContext 구현체를 만들어 보고 1차 캐싱을 적용해보자
엔티티를 어떻게 저장할지 잘 고려해보자
```java
public interface PersistenceContext {

    Object getEntity(Long id);

    void addEntity(Long id, Object entity);

    void removeEntity(Object entity);
}
```
- [ ] PersistenceContext 에서는 insert, delete, select 의 엔터티를 저장한다.
- [ ] 객체의 Id 를 Key 로 엔터티를 관리한다.
- [ ] getEntity 를 통해 해당 엔터티를 조회한다.
- [ ] addEntity 를 통해 insert 나 select 시 엔터티를 추가한다.
- [x] insert 시에는 key 가 없으므로 statement.getGeneratedKeys 를 이용한다.
- [ ] removeEntity 를 통해 delete 시 엔터티를 제거한다.

- 요구사항 2 - snapshot 만들기
- 요구사항 3 - 더티체킹 구현
