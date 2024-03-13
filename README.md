# jpa-entity-manager

### 🚀 1단계 - 엔터티 매핑 (EntityPersister)

#### 요구사항

* [x] 요구사항 1 - 엔터티의 데이터베이스 매핑, 쿼리 생성 및 실행
    * 1주 차에 만들었던 쿼리 빌더를 잘 활용해 보자
    * 쿼리 출력 테스트를 위한 메서드 및 함수를 만들어도 무관

* [x] 요구사항 2 - EntityManager 의 책임 줄여주기
    * EntityManager 의 구현체에서 쿼리 생성 및 데이터 매핑 에 대한 책임을 EntityPersister 로 옮겨주자

### 🚀 2단계 - 엔터티 초기화 (EntityLoader)

#### 요구사항

* [x] 요구사항 1 - RowMapper 리팩터링
    * 아래와 같이 새로운 엔티티가 생길 때마다 매번 RowMapper를 만들어야 할까? 동적으로 생성해 보자
    * > EntityLoader라는 클래스를 만들어 구현<br>
      ResultSet의 metaData를 잘활용해보자<br>
      EntityLoader는 데이터베이스 쿼리를 실행하여 엔티티 객체를 로드하는 역할을 수행합니다.

* [x] 요구사항 2 - EntityManager 의 책임 줄여주기
    * EntityManager 의 구현체에서 find 에 대한 책임을 EntityLoader 로 옮겨주자