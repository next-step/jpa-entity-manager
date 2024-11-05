# jpa-entity-manager

## step 1
- [x] 요구 사항 1 - Persistence Context 구현
  ```java
    public interface EntityManager {}

    public interface PersistenceContext {}
  ```

- [x] 요구 사항 2 - EntityPersister 구현
  ```java
    public class EntityPersister {

        public boolean update(parameters는 자유롭게);
  
        public void insert(parameters는 자유롭게);
  
        public void delete(parameters는 자유롭게);
        
        // ...
    }
  ```

## step 2
- [x] 요구 사항 1 - RowMapper Reflection API 사용해 구현
  - EntityLoader라는 클래스를 만들어 구현
  - EntityLoader 는 엔터티를 데이터베이스에서 로드하고 로드된 엔터티 상태를 영속성 컨텍스트 내에서 추적 및 관리
- [x] 요구 사항 2 - EntityManager, Persistence Context 책임 전가
  - EntityManager의 구현체에서 관련 책임을 EntityLoader로 옮겨주자

## step 3
- [x] 요구 사항 1 - 만들었던 PersistenceContext 에서 효율적인 메모리 관리를 위한 기능 구현 (1차 캐싱)
- [x] 요구 사항 2 - 더티체킹 구현
  - 영속 컨텍스트 내에서 Entity 를 조회
  - 조회된 상태의 Entity 를 스냅샷 생성
  - 트랜잭션 커밋 후 해당 스냅샷과 현재 Entity 를 비교 (데이터베이스 커밋은 신경쓰지 않는다)
  - 다른 점을 쿼리로 생성
