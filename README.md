# jpa-entity-manager

## 1단계 - EntityPersister 구현

### 요구사항1 - Persistence Context 구현
EntityManager 와 Persistence Context 의 차이점을 이해하고 역할을 나누어 구현해보자
> PersistenceContext, EntityManager 에 대한 추상화 설계를 같이 진행해 보자   
```java
public interface EntityManager {
    //...
}

public interface PersistenceContext {
    // 객체를 어떻게 관리할 수 있을지 구현체에서 생각해보자
}


```

### 요구 사항 2 - Persister 구현
> 1주 차에 만들었던 쿼리 빌더를 잘 활용해 보자   
> 쿼리 출력 테스트를 위한 메서드 및 함수를 만들어도 무관

```java
public class EntityPersister {
//- 구현 해보기

// e.g 
// private EntityName entityName;
// private EntityTableName entityTableName;
// private EntityCoulmns entityCoulmns;
// private EntityValues entityValues;
// .... 등

public boolean update(parameters는 자유롭게);

public void insert(parameters는 자유롭게);

public void delete(parameters는 자유롭게);
```
