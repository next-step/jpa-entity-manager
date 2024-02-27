# jpa-entity-manager

# 1단계 - 엔티티 매핑

## 요구사항 1 - 엔티티의 데이터메이스 매핑, 쿼리 생성 및 실행
- [x] update 쿼리 생성
- [x] entity persister를 통한 update 쿼리 실행
- [x] entity persister를 통한 insert 쿼리 실행
- [x] entity persister를 통한 delete 쿼리 실행

## 요구사항 2 - EntityManager의 책임 줄여주기
- [x] EntityManager의 persist, remove 책임 EntityPersister로 이동


# 2단계 - 엔터티 초기화

## 요구사항 1 - RowMapper 리팩터링
- [x] EntityLoader를 통해 조회할 수 있다.
- [x] RowMapperFactory를 이용해 RowMapper를 만들 수 있다.

## 요구사항 2 - EntityManager의 책임 줄여주기
- [x] find 책임을 EntityLoader로 이동


# 3단계 - First Level Cache, Dirty Check

## 요구사항 1 - PersistenceContext 구현체를 만들어보고 1차 캐싱을 적용해보자
- [x] 1차 캐시에서 getEntity 기능 구현
- [x] 1차 캐시에서 addEntity 기능 구현
- [x] 1차 캐시에서 removeEntity 기능 구현

