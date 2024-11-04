# jpa-entity-manager
- [ ] EntityManager
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
- [ ] EntityLoader
  - [ ] load

## EntityPersister
- EntityPersister는 엔터티의 메타데이터와 데이터베이스 매핑 정보를 제공하고, 변경된 엔터티를 데이터베이스에 동기화하는 역할
- EntityPersister 는 엔티티와 데이터베이스 간의 매핑되는 정보를 관리
- EntityPersister 인터페이스 정의서에 find() 메서드는 왜 없을까? ([Hibernate EntityPersister](https://docs.jboss.org/hibernate/orm/5.2/javadocs/org/hibernate/persister/entity/EntityPersister.html))
  - find() 의 역할은 Session(EntityManager) 에서 수행한다.
    - 1차 캐시, 지연 로딩의 고수준 역할을 수행한다.
  - EntityPersister 는 엔티티의 상태변경에 초점을 맞추고 SQL문 생성 및 상태 변경 쿼리 수행과 같은 저수준 작업에 집중한다.

## EntityLoader
- EntityLoader 는 엔터티를 데이터베이스에서 로드하고 로드된 엔터티 상태를 영속성 컨텍스트 내에서 추적 및 관리
- EntityLoader vs EntityPersister
  - EntityPersister 는 '변경'에 초점이 맞춰져 있다. 엔티티가 변경되면 데이터베이스에 동기화한다.
  - EntityLoader 는 '조회(로드)'에 초점이 맞춰져 있다. 
    - 조회를 위해서는 데이터베이스 뿐만 아니라, 영속성 컨텍스트에서의 조회도 포함되기 때문에 영속성 컨텍스트와의 연관관계가 필요하다.
