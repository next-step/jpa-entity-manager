package persistence.sql.dml.builder;

import fixtures.EntityFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.entity.attribute.EntityAttribute;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Nested
@DisplayName("SelectInsertQueryBuilder 클래스의")
public class SelectInsertQueryBuilderTest {

    @Nested
    @DisplayName("prepareStatement 메소드는")
    public class prepareStatement {

        @Nested
        @DisplayName("where절이 주어지면")
        public class withWhereClause {

            @Test
            @DisplayName("적절한 DML을 리턴한다.")
            void returnDmlWithWhereClause() {
                String dml = SelectQueryBuilder.of(EntityAttribute.of(EntityFixtures.SampleOneWithValidAnnotation.class))
                        .where("id", "1").prepareStatement();
                assertThat(dml).isEqualTo("SELECT * FROM entity_name WHERE id = '1'");
            }
        }
    }
}
