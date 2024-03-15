# jpa-entity-manager

## 4단계
- [ ] 요구사항 1 - CRUD 작업 수행시 엔터티의 상태를 추가해보자. (EntryEntity 추가)
- [x] 2단계 피드백 반영
  - [x] merge 테스트 코드 추가, entity와 snpashot은 다를때 값 업데이트하도록 버그 픽스
  - [x] snapshot return 시 optional을 제거하라. 
  - [x] remove 메서드의 파라메터로 entityKey를 전달하라
  - [x] 실제 예외가 발생했을때만 객체를 생성하라.
  - [x] updateEntity 메서드 실행시 EntityCache에도 값을 넣어준다. addEntity의 파라메터로 id도 함께 넘긴다. 
  - [x] entityLoader.find에서 가져오는 객체는 매번 다른 주소값을 리턴하므로 테스트에서 대상에서 제거하라
  - [x] primaryKey로 클래스 네이밍 변경, 패키지 위치 이동, static 메서드 대신 멤버 변수화

### 4단계 질문사항
* [PR 코멘트 링크](https://github.com/next-step/jpa-entity-manager/pull/165#discussion_r1525250415)
* PR 코멘트 스크린샷
![스크린샷 2024-03-16 오전 8.16.12.png](..%2F..%2F..%2F..%2Fvar%2Ffolders%2F_q%2Fd52gkwbn3y539jdm707grkpr0000gn%2FT%2FTemporaryItems%2FNSIRD_screencaptureui_C2M3gP%2F%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202024-03-16%20%EC%98%A4%EC%A0%84%208.16.12.png)
넵 종민님! 질문이 있습니다. 멤버 메서드로 변경시, PrimaryKey 클래스의 name, field, dataTypeName은 getPrimaryKeyValue를 실행시 사용되지 않는데요. 그래서 static으로 선언했던 거였는데, 멤버 메서드로 변경시 어떤 이점이 있을까요?

## 3단계
- [x] 요구사항 1 - PersistenceContext 구현체를 만들어 보고 1차 캐싱을 적용하라
- [x] 요구사항 2 - snapshot을 구현하여라
- [x] 요구사항 3 - dirty checking을 구현하여라.
- [x] 2단계 피드백 반영
  - [x] EOL 반영하라
  - [x] SetClause의 접근제어자 수정하라
  - [x] 클래스의 레이아웃을 수정하라
    - ```
      class A {
        상수(static final) 또는 클래스 변수
        인스턴스 변수
        생성자
        팩토리 메소드
        메소드
        기본 메소드 (equals, hashCode, toString)
      }
      ```

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
