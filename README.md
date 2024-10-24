# jpa-entity-manager

## 프로그래밍 요구 사항

- 모든 로직에 단위 테스트를 구현한다. 단, 테스트하기 어려운 UI 로직은 제외
- 자바 코드 컨벤션을 지키면서 프로그래밍한다.
- 객체지향설계의 규칙을 다 지켜본다.

## 과제 진행 요구 사항

- 기능을 구현하기 전 `README.md`에 구현할 기능 목록을 정리해 추가한다.
- Git의 커밋 단위는 앞 단계에서 `README.md`에 정리한 기능 목록 단위로 추가한다.

## 프로그래밍 요구 사항

### 🚀 1단계 - EntityPersister 구현

- [x] 요구 사항 1 - Persistence Context 구현
- [x] 요구 사항 2 - Persister 구현

### 🚀 2단계 - EntityLoader 구현

- [x] 요구 사항 1 - RowMapper Reflection API 사용해 구현
- [x] 요구 사항 2 - EntityManager, Persistence Context 책임 전가

### 🚀 3단계 - Persistence Context, Dirty Check
- [x] 요구 사항1 - 만들었던 PersistenceContext 에서 효율적인 메모리 관리를 위한 기능 구현 (1차 캐싱)
- [x] 요구 사항2 - 더티체킹 구현

### 🚀 4단계 - EntityEntry
- [x] 요구 사항 1 - Entity 의 라이프 사이클 관리 작업 수행 시 엔터티의 상태를 추가해보자
