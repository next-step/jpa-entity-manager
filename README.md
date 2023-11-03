# jpa-entity-manager


# 1단계 - 

## 0. 요구사항 Update QueryBuilder 생성하기
-  

```sql
UPDATE USERS[TABLE] SET nick_name[COLUMN] = 'value2'[VALUE] WHERE ID[COLUMN] = 6[VALUE];
```

## 1.요구사항 Persister의 엔터티의 데이터베이스 매핑, 쿼리 생성 및 실행


## 2. 요구사항 EntityManager 의 책임 줄여주기


## 테스트 사항
- UpdateQueryBuilder 쿼리 검증 및 수행 검증
- EntityPersister Update 수행 검증
- EntityPersister Insert 수행 검증
- EntityPersister Delete 수행 검증


## 공통 구현 사항


## 고민사항
- 엔티티클래스와 테이블 간의 맵핑 및 데이터베이스 작업(CRUD 작업) 처리
  - EntityPersister(class) 받아서 update, insert, delete 수행하면 된다.
  - Persister에 Entity에 대한 column, table, template 다가져와서 만들어 두면 된다. (캐싱을 이미 해두고 사용하려고 하는것으로 보임) Persister를 불변객체로 사용하면 될듯

- 엔티티 상태 데이터베이스와 동기화
- 캐싱 지원
  - Cache 구현 (Cache의 대상)
  - Manager에서 Cache를 가지고 해당 entity를 managing 할때 가져와서 update, insert, delete를 해주면 된다.


- 해당 구현사항의 의도성이 무엇일까?
  - Hibernate내의 Entity Manager 와 Persister의 책임들을 구현하면서 이해도 를 증가
  - EntityManager에서 EntityPersister로 책임이 이관되면서 refactoring?
  - Cache 구현을 hibernate 내에서 어떻게 진행하는지 보여주기?
  - Connection을 EntityManager가 관리하고 Persister는 받아서 사용하는게 맞아 보인다.
    - hibernate 코드를 읽어야 되는 부분인것같다.(어딜봐야될까?)
    - SessionImpl 가 session을 관리하는것으로 보이니깐. (connection)을 받아오자


## EntityPersister가 하는일


## 미래 추가 구현사항
- Update, Select, Update의 where 절이 중복코드로 나오긴하는데 추상화를 시켜야 될까?
- where builder로 만들어서 clause 추가할 수 있게 만들어보자
- MetaColumns의 field 값과 field value 값이 list로 맵핑되어있는데 해당 값을 map으로 바꿔야 될까?

