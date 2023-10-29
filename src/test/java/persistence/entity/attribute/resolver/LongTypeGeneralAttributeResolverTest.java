package persistence.entity.attribute.resolver;

import fixtures.EntityFixtures;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Nested
@DisplayName("LongTypeGeneralAttributeResolver 클래스의")
class LongTypeGeneralAttributeResolverTest {

    @Nested
    @DisplayName("supports 메소드는")
    class supports {
        @Nested
        @DisplayName("Long 타입이 주어지면")
        class withLong {
            @Test
            @DisplayName("true를 반환한다")
            void supports() {
                GeneralAttributeResolver resolver = new LongTypeGeneralAttributeResolver();
                assertThat(resolver.supports(Long.class)).isTrue();
                assertThat(resolver.supports(Integer.class)).isFalse();
            }
        }
    }

    @Nested
    @DisplayName("resolve 메소드는")
    class resolve {
        @Nested
        @DisplayName("Long 타입의 필드가 주어지면")
        class withField {
            @Test
            @DisplayName("LongTypeGeneralAttribute를 반환한다")
            void returnAttribute() throws NoSuchFieldException {
                GeneralAttributeResolver resolver = new LongTypeGeneralAttributeResolver();
                EntityFixtures.SampleTwoWithValidAnnotation sample
                        = new EntityFixtures.SampleTwoWithValidAnnotation(1L, "민준", 29L);
                Field field = sample.getClass().getDeclaredField("age");

                Assertions.assertDoesNotThrow(() -> resolver.resolve(field));
            }
        }
    }
}
