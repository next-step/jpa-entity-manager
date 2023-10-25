package persistence.sql;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.exception.NoEntityException;
import persistence.fake.FakeDialect;
import persistence.meta.EntityMeta;

class QueryGeneratorTest {
    @Test
    @DisplayName("엔티티클래스정보가 비어 있으면 예외가 발생한다.")
    void emptyClass() {
        assertThatExceptionOfType(NoEntityException.class)
                .isThrownBy(() -> QueryGenerator.of((Class) null, new FakeDialect()));
    }

    @Test
    @DisplayName("엔티티정보가 비어 있으면 예외가 발생한다.")
    void emptyEntityMeta() {
        assertThatExceptionOfType(NoEntityException.class)
                .isThrownBy(() -> QueryGenerator.of((EntityMeta) null, new FakeDialect()));
    }

}
