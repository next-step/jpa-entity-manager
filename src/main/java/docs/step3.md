# 3단계 - First Level Cache, Dirty Check

## 요구사항 1 - PersistenceContext 구현체를 만들어 보고 1차 캐싱을 적용해보자

- 엔티티를 어떻게 저장할지 잘 고려해보자

```java
public interface PersistenceContext {

    Object getEntity(Long id);

    void addEntity(Long id, Object entity);

    void removeEntity(Object entity);
}
```

## 요구사항 2 - snapshot 만들기

```markdown
1. 영속 컨텍스트 내에서 Entity 를 조회
2. 조회된 상태의 Entity 를 스냅샷 생성
3. 트랜잭션 커밋 후 해당 스냅샷과 현재 Entity 를 비교 (데이터베이스 커밋은 신경쓰지 않는다)
4. 다른 점을 쿼리로 생성
```

- getDatabaseSnapshot - 매번 동기화가 일어나서 데이터베이스의 데이터가 매번 방여되는 반면
- getCachedDatabaseSnapshot - 해당 부분은 데이터베이스 연결 없이 메모리에 캐싱되어 있는 것을 활용해 성능을 향상 시키는 메서드네요 (성능 향상은 되지만 정합성에 이슈가 존재)
    - Snapshot 은 Persistence Context 에서 관리되는 영속성 엔티티의 이전 상태를 나타냅니다. 이전 상태는 변경이 일어나기 이전의 상태를 의미합니다.
    - Snapshot 은 영속성 컨텍스트 내부의 데이터를 복사한 것으로, 이를 이용하여 변경 사항을 감지합니다.

## 요구사항 3 - 더티체킹 구현

```java
class CustomJpaRepositoryTest {

    private EntityManager entityManager;

    @Test
    @DisplayName("save 시 dirty checking 로직 구현")
    void saveWithDirty() {

    }
}
```

- 엔티티의 상태를 스냅샷으로 저장하여, 변경된 값이 있는지를 비교하는 방식입니다.
    - 엔티티를 조회할 때 스냅샷을 만들어 둔 후,
    - 엔티티의 상태를 변경할 때마다 스냅샷과 비교하여 변경 내용을 감지합니다.
