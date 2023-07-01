# jpa-entity-manager

# week1

- [X] 위의 정보를 바탕으로 insert 구현해보기
- [X] 위의 정보를 바탕으로 모두 조회(findAll) 기능 구현해보기
- [X] 위의 정보를 바탕으로 단건 조회(findById) 기능 구현해보기
- [X] 위의 정보를 바탕으로 delete 쿼리 만들어보기

# week2

1단계

- [X] 요구사항1 - find
- [X] 요구사항2 - persist (insert)
- [X] 요구사항3 - remove (delete)
  2단계
- [X] 요구사항1 - PersistenceContext 구현체를 만들어 보자
- [X] 요구사항2 - 1차 캐싱 적용
  3단계

#### 시나리오

영속 컨텍스트 내에서 Entity 를 조회
조회된 상태의 Entity 를 스냅샷 생성
트랜잭션 커밋 후 해당 스냅샷과 현재 Entity 를 비교 (데이터베이스 커밋은 신경쓰지 않는다)
다른 점을 쿼리로 생성

- [X] 요구사항1 - snapshot 만들기
- [X] 요구사항2 - 더티체킹 구현
