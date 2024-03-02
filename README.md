# jpa-entity-manager

## 할 일들

### 0단계 - 기본 코드 준비

-[x] jpa-query-builder 코드를 옮겨 온다.

### 1단계 - 엔터티 매핑 (EntityPersister)

- [x] 요구사항 1 - 엔터티의 데이터베이스 매핑, 쿼리 생성 및 실행
- [x] 요구사항 2 - EntityManager 의 책임 줄여주기

### 2단계 - 엔터티 초기화 (EntityLoader)

- [x] 요구사항 1 - RowMapper 리팩터링
- [x] 요구사항 2 - EntityManager 의 책임 줄여주기

### 3단계 - First Level Cache, Dirty Check

- [x] 요구사항1 - PersistenceContext 구현체를 만들어 보고 1차 캐싱을 적용해보자
- [x] 요구사항2 - snapshot 만들기
- [x] 요구사항3 - 더티체킹 구현

### 4단계 - EntityEntry

- [x] 요구사항 1 - CRUD 작업 수행 시 엔터티의 상태를 추가해보자
- [x] // XXX: Difference -> Changes 네이밍 변경
- [x] // FirstLevelCache 를 별도 클래스로 분리
