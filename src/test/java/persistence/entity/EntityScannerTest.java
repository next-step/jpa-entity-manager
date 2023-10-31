package persistence.entity;

import domain.FixtureEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.Application;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class EntityScannerTest {

    @Test
    @DisplayName("EntityScanner 를 통해 특정 클래스포함, 하위 중 @Entity 를 가지고 있는 class 들을 가져올 수 있다.")
    void entityScannerTest() {
        final EntityScanner entityScanner = new EntityScanner(FixtureEntity.class);

        final List<Class<?>> entityClasses = entityScanner.getEntityClasses();

        assertSoftly(softly -> {
            softly.assertThat(entityClasses).contains(FixtureEntity.Person.class);
            softly.assertThat(entityClasses).doesNotContain(FixtureEntity.class);
        });
    }

    @Test
    @DisplayName("EntityScanner 를 통해 @EntityScanBase 를 가지고 있는 클래스 포함, 하위 중 @Entity 를 가지고 있는 class 들을 가져올 수 있다.")
    void entityScannerWithEntityScanBaseAnnotationTest() {
        final EntityScanner entityScanner = new EntityScanner(Application.class);

        final List<Class<?>> entityClasses = entityScanner.getEntityClasses();

        assertSoftly(softly -> {
            softly.assertThat(entityClasses).contains(FixtureEntity.Person.class);
            softly.assertThat(entityClasses).doesNotContain(FixtureEntity.class);
        });
    }

    @Test
    @DisplayName("EntityScanner 를 통해 특정 클래스 밑에 @Entity 를 가진 class 가 없는 경우 emptyList 를 반환한다.")
    void entityScannerReturnEmptyTest() {
        final EntityScanner entityScanner = new EntityScanner(NoEntityIncluded.class);

        final List<Class<?>> entityClasses = entityScanner.getEntityClasses();

        assertThat(entityClasses).isEmpty();
    }

    private static class NoEntityIncluded {

    }
}
