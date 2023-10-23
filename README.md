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
