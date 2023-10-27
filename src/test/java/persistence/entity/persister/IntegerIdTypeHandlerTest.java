package persistence.entity.persister;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.entity.attribute.EntityAttribute;
import persistence.fixture.TestEntityFixture;

import java.lang.reflect.Field;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Nested
@DisplayName("IntegerIdTypeHandler 클래스의 ")
class IntegerIdTypeHandlerTest {
    @Nested
    @DisplayName("support 메소드는 ")
    class support {
        @Nested
        @DisplayName("Integer 클래스 타입이 주어지면")
        class withIntegerClass {
            @Test
            @DisplayName("true를 반환한다.")
            void returnTrue() {
                IdTypeHandler idTypeHandler = new IntegerIdTypeHandler();
                assertThat(idTypeHandler.support(Integer.class)).isTrue();
            }
        }
    }

    @Nested
    @DisplayName("setGeneratedIdToEntity 메소드는 ")
    class setGeneratedIdToEntity {
        @Nested
        @DisplayName("인스턴스와 Id필드 그리고 키값이 주어지면")
        class withValidArgs {
            @Test
            @DisplayName("Id필드에 키값을 셋팅한다.")
            void setKeyAtId() {
                IdTypeHandler idTypeHandler = new IntegerIdTypeHandler();
                EntityAttribute entityAttribute
                        = EntityAttribute.of(TestEntityFixture.EntityWithIntegerId.class);

                Field idField = entityAttribute.getIdAttribute().getField();
                TestEntityFixture.EntityWithIntegerId sample =
                        new TestEntityFixture.EntityWithIntegerId("민준", 29);
                idTypeHandler.setGeneratedIdToEntity(sample, idField, 1);

                assertThat(sample.getId()).isEqualTo(1);
            }
        }
    }
}
