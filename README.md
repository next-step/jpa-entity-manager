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
