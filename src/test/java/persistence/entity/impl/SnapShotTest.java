package persistence.entity.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.dialect.H2ColumnType;

@DisplayName("SnapShot 테스트")
class SnapShotTest {

    @Test
    @DisplayName("SnapShot과 같은지 비교할 수 있다.")
    void snapShotCompareOtherEntity() {
        final SnapShotTestEntity entity1 = new SnapShotTestEntity(1L, "snapshot", "snapshot@gmail.com");
        final SnapShotTestEntity entity2 = new SnapShotTestEntity(1L, "snapshot", "snapshot@gmail.com");
        final SnapShot snapShot = new SnapShot(entity1, new H2ColumnType());

        assertAll(
            () -> assertThat(snapShot.isSameWith(entity2)).isTrue()
        );
    }

    @Test
    @DisplayName("SnapShot과 해당 컬럼에 대한 값이 모두 일치해야 한다.")
    void snapShotCompareMeta() {
        final SnapShotTestEntity entity1 = new SnapShotTestEntity(1L, "snapshot@gmail.com", "snapshot");
        final SnapShotTestEntity entity2 = new SnapShotTestEntity(1L, "snapshot", "snapshot@gmail.com");
        final SnapShot snapShot = new SnapShot(entity1, new H2ColumnType());

        assertAll(
            () -> assertThat(snapShot.isSameWith(entity2)).isFalse()
        );
    }

    @Entity
    private static class SnapShotTestEntity {

        @Id
        private Long id;

        private String name;

        private String email;

        public SnapShotTestEntity(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        protected SnapShotTestEntity() {

        }
    }
}