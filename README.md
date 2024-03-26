# jpa-entity-manager

## 🚀 1단계 - 엔터티 매핑 (EntityPersister)

### 요구사항 1 - 엔터티의 데이터베이스 매핑, 쿼리 생성 및 실행

1. EntityPersister: insert 기능 구현
2. DMLGenerator: update 쿼리 생성 구현
3. EntityPersister: update 기능 구현
4. EntityPersister: delete 기능 구현

### 요구사항 2 - EntityManager 의 책임 줄여주기

1. EntityManager의 insert, delete 책임 줄여주기

## 🚀 2단계 - 엔터티 초기화 (EntityLoader)

### 요구사항 1 - RowMapper 리팩터링

### 요구사항 2 - EntityManager 의 책임 줄여주기

## 🚀 3단계 - First Level Cache, Dirty Check

### 요구사항1 - PersistenceContext 구현체를 만들어 보고 1차 캐싱을 적용해보자

1. entitiesByKey 지연 초기화
2. entitiesByKey 에서 id로 엔티티 조회할 수 있도록 1차 캐싱 적용

### 요구사항2 - snapshot 만들기

1. entitySnapshotsByKey 지연 초기화
2. 엔티티의 식별자 값을 키로 사용하여 해당 엔티티의 이전 상태를 저장

### 요구사항3 - 더티체킹 구현

1. 엔티티를 find 시 스냅샷을 만들어 둔 후,
2. 엔티티를 save 할 때, 스냅샷과 비교하여 변경 내용을 감지
3. CustomJpaRepository 에서 id가 있는 경우 merge, 없는 경우 persist
4. merge 시 변경을 감지하여, 변경된 경우 update

## 🚀 4단계 - EntityEntry

### 요구사항 1 - CRUD 작업 수행 시 엔터티의 상태를 추가해보자

#### SAVING

- 엔터티를 영속성 컨텍스트에 있고 save() 또는 persist() 메서드가 호출되어 엔터티를 데이터베이스에 저장하기 시작했을 때를 나타내는 상태(아직 데이터베이스에 저장이 완료되지 않음)
- 영속성 컨텍스트 내에서 엔터티가 데이터베이스로 저장되는 동안의 **중간 단계**
- Status.SAVING 상태의 엔터티가 데이터베이스를 실행시킬 쿼리가 **성공적으로 만들어지면(엔티티의 값 포함), 엔터티는 Status.MANAGED 상태로 변경**

#### MANAGED

- 엔터티가 영속성 컨텍스트 내에서 관리되고 있는 상태
- 엔터티가 Status.MANAGED 상태에 있으면 **영속성 컨텍스트에서 캐싱**되며, 동일한 엔터티를 조회할 때 캐시에서 먼저 찾습니다.

#### DELETE

- 엔터티가 영속성 컨텍스트 내에서 삭제된 상태 (DELETED 상태로 변환 후 -> 영속성 컨텍스트 제거 -> GONE 으로 변환)
- 엔터티가 Status.DELETED 상태에 있으면 이후의 변경 내용은 무시되며, 엔터티의 삭제 작업이 수행

#### GONE

- 엔터티가 영속성 컨텍스트 내에서 삭제되고, 해당 엔터티를 다시 로드할 수 없는 상태
- 이 상태에 있는 엔터티는 더 이상 영속성 컨텍스트에서 관리되지 않으며, 삭제된 후에 해당 엔터티를 다시 로드하려고 시도하면 ObjectNotFoundException 또는 유사한 예외가 발생

#### LOADING

- 엔터티를 데이터베이스에서 로드(검색)하는 과정 중에 해당 엔터티의 상태를 나타내는 상태(아직 데이터베이스에서의 로드 와 영속성 컨텍스트와의 동기화 작업이 완료되지 않았음)
- 엔터티의 로드 **작업이 완료되면 Status.MANAGED 상태로 변경**
