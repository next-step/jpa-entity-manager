package persistence.context;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.fixture.TestEntityFixture;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Nested
@DisplayName("PersistenceContextImpl 클래스의")
class PersistenceContextImplTest {
    @Nested
    @DisplayName("getEntity 메소드는")
    class getEntity {
        @Nested
        @DisplayName("클래스타입과 아이디가 주어지면")
        class withValidArgs {
            @Test
            @DisplayName("일차 캐시에 객체가 존재하면 객체를 반환하고, 없으면 null을 반환한다.")
            void returnFromFirstCache() {
                TestEntityFixture.SampleOneWithValidAnnotation sample =
                        new TestEntityFixture.SampleOneWithValidAnnotation("민준", 29);
                PersistenceContext persistenceContext = new PersistenceContextImpl();

                TestEntityFixture.SampleOneWithValidAnnotation instance
                        = persistenceContext.getEntity(TestEntityFixture.SampleOneWithValidAnnotation.class, "1");
                assertThat(instance).isEqualTo(null);

                persistenceContext.addEntity(sample, "1");
                TestEntityFixture.SampleOneWithValidAnnotation retrieved
                        = persistenceContext.getEntity(TestEntityFixture.SampleOneWithValidAnnotation.class, "1");
                assertThat(retrieved.toString()).isEqualTo("SampleOneWithValidAnnotation{id=null, name='민준', age=29}");
            }
        }
    }

    @Nested
    @DisplayName("addEntity 메소드는")
    class addEntity {
        @Nested
        @DisplayName("클래스타입과 필드가 주어지면")
        class withValidArgs {

            @Test
            @DisplayName("영속성 컨택스트에 객체를 넣는다")
            void putInstanceToFirstCache() {
                TestEntityFixture.SampleOneWithValidAnnotation sample =
                        new TestEntityFixture.SampleOneWithValidAnnotation("민준", 29);
                PersistenceContext persistenceContext = new PersistenceContextImpl();

                persistenceContext.addEntity(sample, "1");
                TestEntityFixture.SampleOneWithValidAnnotation retrieved
                        = persistenceContext.getEntity(TestEntityFixture.SampleOneWithValidAnnotation.class, "1");

                assertThat(retrieved.toString()).isEqualTo("SampleOneWithValidAnnotation{id=null, name='민준', age=29}");
            }
        }
    }

    @Nested
    @DisplayName("removeEntity 메소드는")
    class removeEntity {
        @Nested
        @DisplayName("인스턴스와 필드가 주어지면")
        class withValidArgs {

            @Test
            @DisplayName("인스턴스를 일차 캐시에서 제거한다.")
            void removeInstanceInFirstCache() {
                TestEntityFixture.SampleOneWithValidAnnotation sample =
                        new TestEntityFixture.SampleOneWithValidAnnotation("민준", 29);
                PersistenceContext persistenceContext = new PersistenceContextImpl();

                persistenceContext.addEntity(sample, "1");
                TestEntityFixture.SampleOneWithValidAnnotation retrieved
                        = persistenceContext.getEntity(TestEntityFixture.SampleOneWithValidAnnotation.class, "1");

                persistenceContext.removeEntity(retrieved, "1");

                assertThat(persistenceContext.getEntity(TestEntityFixture.SampleOneWithValidAnnotation.class, "1"))
                        .isEqualTo(null);
            }
        }
    }
}
