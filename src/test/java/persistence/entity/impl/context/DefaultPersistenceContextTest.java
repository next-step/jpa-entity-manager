package persistence.entity.impl.context;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import persistence.entity.PersistenceContext;
import persistence.entity.impl.context.DefaultPersistenceContext;
import persistence.sql.dialect.H2ColumnType;

@DisplayName("PersistenceContext 테스트")
class DefaultPersistenceContextTest {

    private PersistenceContext defaultPersistenceContext;

    @BeforeEach
    void setUp() {
        defaultPersistenceContext = new DefaultPersistenceContext(new H2ColumnType());
    }

    @TestFactory
    @DisplayName("PersistenceContext에 엔티티를 관리할 수 있다.")
    public Stream<DynamicTest> savePersistenceContext() {
        //given
        PersistenceContextEntityTest entity = new PersistenceContextEntityTest(
            1L,
            "persistence",
            "persistence@gmail.com"
        );

        return Stream.of(
            dynamicTest("PersistenceContext에 Entity를 관리대상으로 추가할 수 있다.", () -> {
                assertThatCode(() -> defaultPersistenceContext.addEntity(entity))
                    .doesNotThrowAnyException();
            }),
            dynamicTest("PersistenceContext에 관리대상에 추가된 Entity를 갖고올 수 있다.", () -> {
                final Object addedEntity = defaultPersistenceContext.getEntity(entity.getClass(), entity.getId()).get();
                assertThat(addedEntity == entity).isTrue();
            }),
            dynamicTest("PersistenceContext에 관리대상에 추가된 Entity를 삭제할 수 있다.", () -> {
                defaultPersistenceContext.removeEntity(entity);
                final Optional<Object> purgedEntity = defaultPersistenceContext.getEntity(entity.getClass(), entity.getId());

                assertThat(purgedEntity.isEmpty()).isTrue();
            })
        );
    }

    @Entity
    private static class PersistenceContextEntityTest {

        @Id
        private Long id;

        private String name;

        private String email;

        public PersistenceContextEntityTest(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        protected PersistenceContextEntityTest() {

        }

        public Long getId() {
            return id;
        }
    }
}