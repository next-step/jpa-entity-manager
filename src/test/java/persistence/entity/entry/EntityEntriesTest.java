package persistence.entity.entry;

import fixtures.EntityFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Nested
@DisplayName("EntityEntries 클래스의")
class EntityEntriesTest {

    @Nested
    @DisplayName("changeOrSetStatus 메소드는")
    class changeOrSetStatus {

        @Nested
        @DisplayName("status, 클래스 정보, 인스턴스 아이디가 주어지면")
        class withValidArgs {

            @Test
            @DisplayName("매핑되는 EntityEntry의 Status를 변경한다.")
            void changeOrSetStatus() {
                EntityFixtures.SampleOneWithValidAnnotation sample
                        = new EntityFixtures.SampleOneWithValidAnnotation(1L, "민준", 29);
                EntityEntries entityEntries = new EntityEntries();
                entityEntries.changeOrSetStatus(Status.MANAGED, sample.getClass(), "1");

                assertThat(entityEntries.getEntityEntry(sample.getClass(), "1").getStatus())
                        .isEqualTo(Status.MANAGED);
            }
        }
    }

    @Nested
    @DisplayName("getEntityEntry 메소드는")
    class getEntityEntry {

        @Nested
        @DisplayName("클래스 정보, 인스턴스 아이디가 주어지면")
        class withValidArgs {

            @Test
            @DisplayName("매핑된 EntityEntry를 리턴한다.")
            void getEntityEntry() {
                EntityFixtures.SampleOneWithValidAnnotation sample
                        = new EntityFixtures.SampleOneWithValidAnnotation(1L, "민준", 29);
                EntityEntries entityEntries = new EntityEntries();
                entityEntries.changeOrSetStatus(Status.MANAGED, sample.getClass(), "1");

                assertThat(entityEntries.getEntityEntry(sample.getClass(), "1").getStatus())
                        .isEqualTo(Status.MANAGED);
            }
        }
    }
}
