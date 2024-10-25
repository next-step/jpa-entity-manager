# jpa-entity-manager

## 요구사항
### 1단계 - EntityPersister 구현
- [X] 영속성 컨텍스트에 Entity객체를 저장
- [X] 영속성 컨텍스트에 저장되어있는 Entity 객체를 가져온다.
- [X] 영속성 컨텍스트에 저장되어있는 Entity 객체를 수정한다.
- [X] 영속성 컨텍스트에 저장되어있는 Entity 객체를 제거한다.

### 2단계 - EntityLoader 구현
- [X] EntityLoader를 생성한다.
- [X] EntityPersister의 책임을 EntityLoader로 인가한다.
- [X] Persist로 Person 저장 후 영속성 컨텍스트에 존재하는지 확인한다.
- [X] remove 실행하면 영속성 컨텍스트에 데이터가 제거된다.
- [X] update 실행하면 영속성컨텍스트 데이터도 수정된다.

### 3단계 - Persistence Context, Dirty Check
- [X] 영속성 컨텍스트에서 스냅샷을 생성한다.
- [X] 영속성 컨텍스트에서 스냅샷을 가져온다.
- [X] SnapShot에 저장된 객체와 비교하여 DirtyChecking을 한다.
- [X] SnapShot에 저장된 객체와 비교하여 DirtyChecking을 할시, 데이터가 다른점이 없으면 예외를 발생시킨다.
- [X] DirtyCheck를 하여 쿼리를 생성한다.