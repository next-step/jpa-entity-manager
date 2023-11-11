package persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.builder.BuilderTest;
import persistence.sql.fixture.PersonFixtureStep3;
import persistence.sql.fixture.PersonInstances;

public class JdbcPersistenceContextTest extends BuilderTest {

  public static PersistenceContext persistenceContext;

  @Test
  @DisplayName("context에서 entity를 추가한뒤 반환한다.")
  public void getEntity() {
    persistenceContext = new JdbcPersistenceContext();

    persistenceContext.addEntity(PersonInstances.첫번째사람.getId(), PersonInstances.첫번째사람);
    Object entity = persistenceContext.getEntity(PersonInstances.첫번째사람.getId(),
        PersonInstances.첫번째사람.getClass()).get();

    assertThat(entity).isEqualTo(PersonInstances.첫번째사람);
    assertThat(entity == PersonInstances.첫번째사람).isEqualTo(true);
  }

  @Test
  @DisplayName("context에 entity를 제거한다.")
  public void removeEntity() {
    persistenceContext = new JdbcPersistenceContext();

    persistenceContext.addEntity(PersonInstances.첫번째사람.getId(), PersonInstances.첫번째사람);
    persistenceContext.removeEntity(PersonInstances.첫번째사람);

    Optional<Object> entity = persistenceContext.getEntity(PersonInstances.첫번째사람.getId(),
        PersonFixtureStep3.class);

    assertThat(entity.isEmpty()).isEqualTo(true);
  }

  @Test
  @DisplayName("context에서 스냅샷 entity를 추가한 뒤 변경감지 했을때 해당 entity가 동일하다.")
  public void putSnapshotEntity() {
    persistenceContext = new JdbcPersistenceContext();

    persistenceContext.putDatabaseSnapshot(PersonInstances.첫번째사람.getId(), PersonInstances.첫번째사람);

    boolean changed = persistenceContext.isSameWithSnapshot(PersonInstances.첫번째사람.getId(), PersonInstances.첫번째사람);

    assertThat(changed).isEqualTo(true);
  }

  @Test
  @DisplayName("entity를 snapshot과 비교해서 변경감지한다.")
  public void removeSnapshotEntity() {
    persistenceContext = new JdbcPersistenceContext();

    persistenceContext.putDatabaseSnapshot(PersonInstances.첫번째사람.getId(), PersonInstances.첫번째사람);

    PersonInstances.첫번째사람.setName("Sichang Park");
    PersonInstances.첫번째사람.setAge(22);

    boolean changed = persistenceContext.isSameWithSnapshot(PersonInstances.첫번째사람.getId(), PersonInstances.첫번째사람);

    assertThat(changed).isEqualTo(false);
  }
}
