# 개선할 만한 부분

- Query 들 빌드패턴 적용
- JavaTypes 사용해서 타입 관련 코드 개선
- EntityManagerImpl 에서 PERSON_ROW_MAPPER 구현을 일반화시키기
- mock 대신 h2 사용한 방식으로 테스트코드 작성(delete test) -- 여러 테스트에서 사용할 수 있게..
- ParameterizedTest 의 MethodSource 를 fixture, constant class, enum(써봄) 중 하나로 바꿔보기
- dialect 개념을 만들고 typeconverter 를 집어넣기

# 커밋 전에 확인할 것

* import 정리
* else 키워드를 줄여보기
* 테스트 코드는 충분한가?
* TODO 를 잘 마무리했는가?
* 생성자에 접근자를 잘 적용했나?
* 일관되게 작성했나? (비슷한 일을 하는 클래스가 여러 스타일로 작성됐는가?)
* 불필요하게 쪼개져있진 않은가?
* public/private modifier 기준 소트
