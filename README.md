# jpa-entity-manager

### 🚀 4단계 - EntityEntry
- [ ] 요구사항 1 - CRUD 작업 수행 시 엔터티의 상태를 추가해보자
  - EntityEntry 클래스는 엔터티의 영속성 상태와 상태 변화, 생명주기와 변경감지에 중요한 역할
  - 엔터티를 관리하고 있는 구현체에 상태를 추가하는 기능을 넣어보자
    ```java
    entityEntry.updateStatus(Status status);
    ```
