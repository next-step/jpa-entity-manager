# jpa-entity-manager

## 3단계
- [ ] 2단계 피드백 반영
  - [x] EOL 반영하라
  - [x] SetClause의 접근제어자 수정하라

## 2단계
- [x] 요구사항 1 - RowMapper 리팩터링
- [x] 요구사항 2 - EntityManager 의 구현체에서 find 에 대한 책임을 EntityLoader 로 옮겨주자
- [x] 1단계 피드백 반영
  - [x] find에서 2개 이상 데이터 조회될 경우 예외를 던져라
  - [x] query를 별도 변수로 분리하라.
  - [x] update 쿼리에서 id를 받는 부분과 받지 않는 부분을 분리하여라.
  - [x] 테스트시 h2는 한번만 초기화하라.
  - [x] ValueClause의 getValue에 대해 테스트 코드를 추가하여라.
## 1단계
- [x] 요구사항 1 - 엔터티의 데이터 페이스 매핑, 쿼리 생성 및 실행
- [x] 요구사항 2 - EntityManager의 책임 줄여주기
 
## 0단계
- [x] jpa-query-builder 코드를 옮겨 온다.
