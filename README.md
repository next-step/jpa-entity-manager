# jpa-entity-manager

## Entity 구현

### step 0. 기본 코드 준비

+ [x] 미션 시작 버튼을 눌러 미션을 시작한다.
+ [x] 저장소내에 Github 사용자 이름으로 브랜치가 생성되었는지 확인한다.
+ [x] 저장소를 내 계정으로 포크한다.
+ [x] jpa-query-builder 에 있던 코드를 복사해서 붙여넣는다.

### step 1. EntityPersister 구현
+ [x] Persistence Context를 구현한다.
+ [x] Persister 구현체를 구현한다.

### step 2. EntityLoader 구현
+ [x] EntityLoader 구현체를 구현한다. 
  + ResultSet의 메타데이터를 잘 이용하자
  + 데이터베이스 쿼리를 실행하여 엔티티 객체를 로드하는 역할을 수행한다.
+ [x] EntityManager, Persistence Context 책임 전가

### step 3. Persistence Context와 Dirty Checking 구현
+ [x] Persistence Context에서 효율적인 메모리 관리를 위한 기능 구현
+ [x] Dirty Checking 기능 구현