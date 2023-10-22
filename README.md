# jpa-entity-manager

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

## 요구사항 2
### EntityManager 의 책임 줄여주기
