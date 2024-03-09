## 3/2

### ResultSetMetaData 활용

```markdown
- ResultSet 조회해서 serializer 된 데이터 가져온다.
- 생명주기가 동일하면 EntityManager 에서 PersistenceContext 를 주입 받을 필요 없이,
- this.persistenceContext = new DefaultPersistenceContext 로 넣어줄 수 있다.

- 강의에서는 EntityManager 에서 JdbcTemplate, EntityPersister, EntityLoader, PersistenceContext 주입 받음.
- entitySnapshotsByKey : 데이터베이스 동기화, 즉 데이터를 맞춰준다.
- 실제 Hibernate 구현 내용을 보면, StatefulPersistenceContext 참고하기

- fireLoad 에서 fire : 실행하다의 의미
- proxy callback lazyLoading
```
