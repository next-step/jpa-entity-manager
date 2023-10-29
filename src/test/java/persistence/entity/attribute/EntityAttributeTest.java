package persistence.entity.attribute;

import fixtures.EntityFixtures;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.converter.SqlConverter;
import persistence.sql.ddl.wrapper.CreateDDLWrapper;
import persistence.sql.ddl.wrapper.DropDDLWrapper;
import persistence.sql.infra.H2SqlConverter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Nested
@DisplayName("EntityAttribute 클래스의")
public class EntityAttributeTest {
    SqlConverter sqlConverter = new H2SqlConverter();

    @Nested
    @DisplayName("of 메소드는")
    class of {
        @Nested
        @DisplayName("유효한 클래스 정보와 파서가 들어오면")
        class withValidArgs {
            @Test
            @DisplayName("EntityAttribute를 반환한다.")
            void returnEntityAttribute() {
                EntityAttribute entityAttribute =
                        EntityAttribute.of(EntityFixtures.SampleOneWithValidAnnotation.class);

                Assertions.assertAll(
                        () -> assertThat(entityAttribute.getTableName())
                                .isEqualTo("entity_name"),
                        () -> assertThat(entityAttribute.prepareDDL(new CreateDDLWrapper(sqlConverter)))
                                .isEqualTo("CREATE TABLE entity_name ( " +
                                        "id BIGINT GENERATED BY DEFAULT AS IDENTITY, name VARCHAR(200), " +
                                        "old INTEGER NOT NULL );"),
                        () -> assertThat(entityAttribute.prepareDDL(new DropDDLWrapper(sqlConverter)))
                                .isEqualTo("DROP TABLE entity_name;")
                );
            }
        }

        @Nested
        @DisplayName("@Id 가 여러개인 클래스 정보와 파서가 들어오면")
        class withMultiIdClass {
            @Test
            @DisplayName("예외를 반환한다.")
            void throwException() {
                Assertions.assertThrows(
                        IllegalStateException.class, () -> EntityAttribute.of(
                                EntityFixtures.EntityWithMultiIdAnnotation.class));
            }
        }

        @Nested
        @DisplayName("@Entity 가 없는 클래스 정보와 파서가 들어오면")
        class withEntityWithOutEntityAnnotation {
            @Test
            @DisplayName("예외를 반환한다.")
            void throwException() {
                Assertions.assertThrows(
                        IllegalStateException.class, () -> EntityAttribute.of(
                                EntityFixtures.EntityWithOutEntityAnnotation.class));
            }
        }
    }
}
