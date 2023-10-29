package persistence.entity.attribute.resolver;

import fixtures.EntityFixtures;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Nested
@DisplayName("IntegerTypeIdAttributeResolver 클래스의")
class IntegerTypeIdAttributeResolverTest {
    @Nested
    @DisplayName("supports 메소드는")
    class supports {
        @Nested
        @DisplayName("Integer가 주어지면")
        class withInteger {
            @Test
            @DisplayName("true를 반환한다")
            void supports() {
                IdAttributeResolver resolver = new IntegerTypeIdAttributeResolver();
                assertThat(resolver.supports(Integer.class)).isTrue();
            }
        }
    }

    @Nested
    @DisplayName("resolve 메소드는")
    class resolve {
        @Nested
        @DisplayName("Integer 타입의 필드가 주어지면")
        class withField {
            @Test
            @DisplayName("IntegerTypeIdAttribute를 반환한다")
            void returnAttribute() throws NoSuchFieldException {
                IdAttributeResolver resolver = new IntegerTypeIdAttributeResolver();
                EntityFixtures.EntityWithIntegerId sample
                        = new EntityFixtures.EntityWithIntegerId(1, "민준", 29);
                Field field = sample.getClass().getDeclaredField("id");

                Assertions.assertDoesNotThrow(() -> resolver.resolve(field));
            }
        }
    }

    @Nested
    @DisplayName("setIdToEntity 메소드는")
    class setIdToEntity {
        @Nested
        @DisplayName("인스턴스와 이이디 필드 그리고 벨류가 주어지면")
        class withValidArgs {
            @Test
            @DisplayName("인스턴스 아이디 필드에 아이디 벨류를 세팅한다.")
            void setIdToEntity() throws NoSuchFieldException, IllegalAccessException {
                IdAttributeResolver resolver = new IntegerTypeIdAttributeResolver();
                EntityFixtures.EntityWithIntegerId sample
                        = new EntityFixtures.EntityWithIntegerId("민준", 29);

                Field field = sample.getClass().getDeclaredField("id");

                resolver.setIdToEntity(sample, field, 10);

                assertThat(field.get(sample)).isEqualTo(10);
            }
        }
    }
}
