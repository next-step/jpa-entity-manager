# jpa-entity-manager

### 1단계 - 엔터티 매핑 (EntityPersister)
```
- EntityPersister는 엔터티의 메타데이터와 데이터베이스 매핑 정보를 제공하고, 변경된 엔터티를 데이터베이스에 동기화하는 역할
```
```java
public class EntityPersister {
- 구현 해보기

// e.g 
// private EntityName entityName;
// private EntityTableName entityTableName;
// private EntityCoulmns entityCoulmns;
// private EntityValues entityValues;
// .... 등

public boolean update(parameters는 자유롭게)

public void insert(parameters는 자유롭게)

public void delete(parameters는 자유롭게)
...
}
```
- EntityPersister
  - EntityClass를 필드로 가진다.
  - 쿼리를 실제로 생성하고 전달하는 역할을 한다.
  - parameter가 현재 EntityPersister가 관리하는 entityclass와 다를 경우 예외가 발생한다.
    - 해당 에러는 EntityClass에서 validation하고 있다.
- UpdateQueryBuilder
  - entity의 필드를 update한다.
  - where 절의 조건은 id = 1이다.

#### 요구사항2
- EntityManager 의 구현체에서 쿼리 생성 및 데이터 매핑 에 대한 책임을 EntityPersister 로 옮긴다.

- EntityPersisters
  - entity의 class에 맞는 EntityPerister를 반환한다.
    - 맞는 EntityPersister가 없는 경우 예외가 발생한다.

### 2단계 - 엔터티 초기화 (EntityLoader)
```
- EntityLoader 는 엔터티를 데이터베이스에서 로드하고 로드된 엔터티 상태를 영속성 컨텍스트 내에서 추적 및 관리
```
- EntityLoader
  - select 쿼리를 실행하여 엔티티 객체에 로드한다.
- ReflectionRowMapper
  - clazz를 받아 계속 생성하는데, 이미 생성한 ReflectionRowMapper를 캐싱하여 반환할 수 있도록 한다.

### 3단계 - First Level Cache, Dirty Check
#### 요구사항1
- PersistenceContext 구현체를 만들고, 1차 캐싱을 적용한다.
```java
public interface PersistenceContext {

    Object getEntity(Long id);

    void addEntity(Long id, Object entity);

    void removeEntity(Object entity);
}
```
- PersistenceContext
  - getEntity
    - Map에 저장되어있는 entity를 반환한다.
    - 존재하지 않는다면 null을 반환한다.
  - addEntity
    - entity를 넣는다.
  - removeEntity
    - entity를 삭제한다.
- EntityManager
  - find
    - PersistenceContext에서 검색 후 없으면 EntityLoader에서 검색 후 PersistenceContext에 저장한다.
  - persist
    - PersistenceContext에 이미 entity가 영속화되어있으면 예외가 발생한다. (이걸 addEntity에서 해야하나?)
    - EntityPersister에서 insert한 후 PersistenceContext에 넣는다.
  - remove
    - 영속화 되어있지 않은 entity를 제거하려하는 경우 예외가 발생한다.
    - EntityPersister에서 제거 및 PersistenceContext에도 제거한다.

#### 요구사항2
```
1. 영속 컨텍스트 내에서 Entity 를 조회
2. 조회된 상태의 Entity 를 스냅샷 생성
3. 트랜잭션 커밋 후 해당 스냅샷과 현재 Entity 를 비교 (데이터베이스 커밋은 신경쓰지 않는다)
4. 다른 점을 쿼리로 생성
```
```java
public interface PersistenceContext {
    // ...

    /*
    스냅샷을 만들 때 Object 가 아니라 EntityPersister 라는 인터페이스를 활용해 엔티티가 영속화 될 때 
    데이터베이스로 부터 데이터를 pesister.getDatabaseSnapshot 메서드를 통해 가져옴 
    너무 많은 로직이 있기에 간단하게 구현
     */
    Object getDatabaseSnapshot(Long id, Object entity);

    Object getCachedDatabaseSnapshot(Long id);
    
    // ....
```
- getDatabaseSnapshot
  - EntitySnapShot을 저장한다.
- getCachedDatabaseSnapshot 
  - 저장된 EntitySnapshot을 가져온다.
- EntitySnapShot
  - EntityColumn과 그에 따른 필드를 가지고 있다.
  - Object인 Entity를 받아 변경된 entity field 데이터를 뽑아낼 수 있다. 
- EntityManager
  - snapshot도 entires와 동일하게 동기화해준다.
  - merge
    - 들어온 entity가 snapshot과 동일한지 확인하고 update한다.
    - update한 후 persistenceContext를 동기화한다.
    - snapshot이 없는 경우 find하여 가져와야 한다.

#### 요구사항3
```java
public class CustomJpaRepository<T, ID> {
    private final EntityManager entityManager;
    
    public CustomJpaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    public T save(T t) {
    // 트랜잭션은 신경 쓰지말고 구현해보자
   }
...
}
```
- 처음 저장한 entity의 경우 persist한다.
- 처음 저장한 entity가 아닌 경우 merge한다.
