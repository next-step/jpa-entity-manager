# jpa-entity-manager

## 🗒 추가 개선 사항
- ✅️ 모든 ```@Entity```에 대해 ```Persister``` 초기화 처리에 대하여 고민해보기
- ☑️ ```DIRECT``` 시간 여유가 될 때 도입해보기

## 📍0단계
✅ 기본 코드 준비

## 📍 1단계 - 엔터티 매핑 (EntityPersister)
### 엔터티의 데이터베이스 매핑, 쿼리 생성 및 실행
- ✅ update query 빌더 생성
- ✅ EntityPersister 구현
  - ✅️insert() 구현
    - ✅️ 성공적으로 데이터를 저장한다.
    - ✅️ 존재하지 않는 테이블에 데이터 저장시 오류 출력
    - ✅️ 중복되는 데이터 저장 시도시 오류 출력
  - ✅️ delete() 구현
    - ✅️성공적으로 데이터를 삭제한다.
    - ✅️존재하지 않는 테이블에 데이터 삭제시 오류 출력
  - ✅ update() 구현
    - ✅️ 성공적으로 데이터를 수정한다.
    - ✅️ 존재하지 않는 테이블에 데이터 저장시 오류 출력

### EntityManager 의 책임 줄여주기
- ✅ JdbcTemplate을 EntityPersister로 위임


## 📍 2단계 - 엔터티 초기화 (EntityLoader)
### RowMapper 리팩터링
- ✅ 별도 ResultMapper 구현체 생성

### EntityManager의 책임 줄여주기
- ✅기존 Persister에 작성 되어있던 ```조회 기능``` EntityLoader로 이동
