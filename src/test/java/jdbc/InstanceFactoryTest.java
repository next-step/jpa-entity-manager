package jdbc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.fixture.EntityWithId;
import persistence.fixture.EntityWithoutDefaultConstructor;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class InstanceFactoryTest {
    @DisplayName("인스턴스를 생성한다.")
    @Test
    void createInstance() {
        // given
        final InstanceFactory<EntityWithId> instanceFactory = new InstanceFactory<>(EntityWithId.class);

        // when
        final EntityWithId instance = instanceFactory.createInstance();

        // then
        assertThat(instance).isNotNull();
    }

    @DisplayName("기본 생성자가 없는 클래스로 인스턴스를 생성하면 예외를 발생한다.")
    @Test
    void createInstance_exception() {
        // given
        final InstanceFactory<EntityWithoutDefaultConstructor> instanceFactory = new InstanceFactory<>(EntityWithoutDefaultConstructor.class);

        // when & then
        assertThatThrownBy(instanceFactory::createInstance)
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(InstanceFactory.NO_DEFAULT_CONSTRUCTOR_FAILED_MESSAGE);
    }

    @DisplayName("인스턴스를 복사본을 생성한다.")
    @Test
    void copy() {
        // given
        final EntityWithId entity = new EntityWithId(1L, "Jaden", 30, "test@email.com");
        final InstanceFactory<EntityWithId> instanceFactory = new InstanceFactory<>(EntityWithId.class);

        // when
        final EntityWithId copiedEntity = instanceFactory.copy(entity);

        // then
        assertAll(
                () -> assertThat(copiedEntity).isNotNull(),
                () -> assertThat(copiedEntity.getId()).isEqualTo(entity.getId()),
                () -> assertThat(copiedEntity.getName()).isEqualTo(entity.getName()),
                () -> assertThat(copiedEntity.getAge()).isEqualTo(entity.getAge()),
                () -> assertThat(copiedEntity.getEmail()).isEqualTo(entity.getEmail()),
                () -> assertThat(copiedEntity.getIndex()).isEqualTo(entity.getIndex())
        );
    }
}
