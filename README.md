# jpa-entity-manager

## Step1
- [x] Persistence Context 설계
- [x] Persister 구현

### 설계
- EntityManager는 Entity를 관리하는 주체이다. 
- Entity를 관리하기 위해서는 Entity를 저장하는 저장소가 필요하다. 
- 이 저장소를 Persistence Context라고 한다. 
- Persistence Context는 Entity를 저장하는 저장소이다. 
- Entity를 저장하는 저장소는 Entity를 저장하는 방법을 알고 있어야 한다. 
- 이를 위해서 Persister를 구현한다. 

## Step2
- [x] EntityLoader를 구현한다.


## Step3
- [x] PersistenceContext 생성
- [x] 1차 캐싱
- [x] Dirty Checking

## Step4
- [ ] EntityEntry를 구현한다.
