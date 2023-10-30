package persistence.entity;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("엔티티 클래스 로더 테스트")
class EntityClassLoaderTest {

    @Test
    @DisplayName("존재하지 않는 패키지이면 예외가 발생한다.")
    void noPackage() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new EntityClassLoader("noPackage"));

    }

    @Test
    @DisplayName("엔티티 클래스들을 로드 한다.")
    void entity() {
        //givne
        EntityClassLoader entityClassLoader = new EntityClassLoader("persistence.testFixtures");

        //when
        final Set<Class<?>> entityClasses = entityClassLoader.getEntityClasses();

        //then
        assertThat(entityClasses).hasSize(5);
    }

}
