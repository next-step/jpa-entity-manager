package persistence.entity.attribute.resolver;

import fixtures.EntityFixtures;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Nested
@DisplayName("IntegerTypeGeneralAttributeResolver 클래스의")
class IntegerTypeGeneralAttributeResolverTest {

    @Nested
    @DisplayName("supports 메소드는")
    class supports {
        @Nested
        @DisplayName("Integer가 주어지면")
        class withInteger {
            @Test
            @DisplayName("true를 반환한다")
            void supports() {
                GeneralAttributeResolver resolver = new IntegerTypeGeneralAttributeResolver();
                assertThat(resolver.supports(Integer.class)).isTrue();
                assertThat(resolver.supports(Long.class)).isFalse();
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
            @DisplayName("IntegerTypeGeneralAttribute를 반환한다")
            void returnAttribute() throws NoSuchFieldException {
                GeneralAttributeResolver resolver = new IntegerTypeGeneralAttributeResolver();
                EntityFixtures.SampleOneWithValidAnnotation sample
                        = new EntityFixtures.SampleOneWithValidAnnotation(1L, "민준", 29);
                Field field = sample.getClass().getDeclaredField("age");

                Assertions.assertDoesNotThrow(() -> resolver.resolve(field));
            }
        }
    }
}
