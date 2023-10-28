package persistence.sql.entitymetadata.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EntityTableTest {

    @Entity
    static class FakeNoneTableAnnotationEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    }

    @Table(name = "fake_entity")
    @Entity
    static class FakeTableAnnotationEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    }

    @Table
    @Entity
    static class FakeTableAnnotationDefaultNameEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    }

    @DisplayName("엔티티 테이블 메타데이터 생성 (어노테이션 미존재시)")
    @Test
    void entityTableName_1() {
        EntityTable<FakeNoneTableAnnotationEntity> entityTable = new EntityTable<>(FakeNoneTableAnnotationEntity.class);

        assertThat(entityTable.getName()).isEqualTo("FakeNoneTableAnnotationEntity");
    }

    @DisplayName("엔티티 테이블 메타데이터 생성 (어노테이션 존재시)")
    @Test
    void entityTableName_2() {
        EntityTable<FakeTableAnnotationEntity> entityTable = new EntityTable<>(FakeTableAnnotationEntity.class);

        assertThat(entityTable.getName()).isEqualTo("fake_entity");
    }

    @DisplayName("엔티티 테이블 메타데이터 생성 (어노테이션 존재하지만 name value 없을 경우)")
    @Test
    void entityTableName_3() {
        EntityTable<FakeTableAnnotationDefaultNameEntity> entityTable = new EntityTable<>(FakeTableAnnotationDefaultNameEntity.class);

        assertThat(entityTable.getName()).isEqualTo("FakeTableAnnotationDefaultNameEntity");
    }
}
