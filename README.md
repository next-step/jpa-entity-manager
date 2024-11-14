# jpa-entity-manager
### TODO
#### EntityManager
- [ ] find
- [ ] persist
- [ ] remove
- [ ] PersistenceContext
    - [ ] Entity 조회
        - 동일한 트랜잭션 내에서 동일한 엔티티를 조회하면 데이터베이스에 다시 접근하지 않고 메모리에서 가져온다.
    - [ ] Entity 추가
        - 신규 Entity 를 관리 대상에 추가한다.
    - [ ] Entity 삭제
- [ ] EntityPersister
    - [ ] update
    - [ ] insert
    - [ ] delete

- EntityPersister 는 엔티티와 데이터베이스 간의 매핑되는 정보를 관리할 수있게.. 
- EntityPersister 인터페이스 정의서에 find() 메서드는 없음.
    - find() 의 역할은 Session(EntityManager) 에서 수행한다. 즉, 1차 캐시, 지연 로딩의 고수준 역할을 수행.
    - EntityPersister 는 엔티티의 상태변경에 초점을 맞추고 SQL문 생성 및 상태 변경 쿼리 수행과 같은 저수준 작업