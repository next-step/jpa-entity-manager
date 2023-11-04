# jpa-entity-manager


# 1단계 - 엔티티 맵핑 EntityPersister

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

## 미래 추가 구현사항
- Update, Select, Update의 where 절이 중복코드로 나오긴하는데 추상화를 시켜야 될까?
- where builder로 만들어서 clause 추가할 수 있게 만들어보자
- MetaColumns의 field 값과 field value 값이 list로 맵핑되어있는데 해당 값을 map으로 바꿔야 될까?



# 2단계 - 엔티티 초기화 EntityLoader

## 1. 요구사항 RowMapper 리팩터링
  - 해당 요구사항을 query builder에서 만족한것같다.
  - 아마 여기서 parameterized ROWMAPPER를 구현하길 원하신것 같다.

## 2. 요구사항 EntityManager 의 책임 줄여주기
  - Loader로 entity를 가져오는 역할을 주었습니다.
  - 복수의 entity는 아직 가져오는게 안되있는데 이부분은 조금 어떻게 구현해야할지 궁금하다.

## 고민사항
  - MetaEntity에 대한 의존성이 점점 줄고있는데 제거하는 방향이 맞을까요?
    - RowMapper에서 MetaEntity를 생성하면서 불필요한 객체를 다 만들고 있긴합니다.
    - 하지만 instance를 만드는 책임을 누구에게 줘야할지 궁금합니다.
  - 이제 Exception에 대한 걱정을 하기 시작
    - Exception 처리를 할 부분은 constructor 가 없을때 (instance 못만듬)
    - Custom Exception도 처리하길 원하시나요??
  - 지연 로딩 구현 
    - 이미 있는 값이면 먼저 가져오기? entity들을 Loader가 cache 해두는 느낌인듯?
## 테스트 사항
- UpdateQueryBuilder 쿼리 검증 및 수행 검증
- EntityPersister Update 수행 검증

## 리뷰사항
https://github.com/next-step/jpa-entity-manager/pull/68
1. MetaData 이용, QueryBuilder 한군데 모아두기  --> 이것은 MetaData 와 parse, mapping 로직 에 전반적 객체지향적 수정 필요 일단 CreateBuilder 로직만 이전
2. WhereClauseBuilder
3. delete 시에 대한 예외처리
4. update 시에 예외처리
5. Persist 할때 id 있을때랑 없을때 확인해서 넣어주기ㄴ

