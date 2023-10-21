package persistence.sql.dml;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.sql.dml.builder.DMLQueryBuilder;
import persistence.sql.dml.builder.DMLQueryFactory;
import persistence.sql.dml.builder.InsertDMLQueryBuilder;
import persistence.sql.dml.model.DMLType;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Nested
@DisplayName("DMLQueryFactory 클래스의")
public class DMLQueryFactoryTest {
    @Nested
    @DisplayName("createQueryBuilder 메소드는")
    class prepareStatement {
        @Nested
        @DisplayName("DMLType.INSERT 가 주어지면 메소드는")
        class withInsertDML {
            @Test
            @DisplayName("Insert 빌더를 생성한다.")
            void returnInsertBuilder() {
                DMLQueryBuilder dmlQueryBuilder = DMLQueryFactory.createQueryBuilder(DMLType.INSERT);
                assertThat(dmlQueryBuilder).isInstanceOf(InsertDMLQueryBuilder.class);
            }
        }
    }
}
