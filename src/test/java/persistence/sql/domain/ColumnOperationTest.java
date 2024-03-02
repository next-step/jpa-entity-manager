package persistence.sql.domain;

import jakarta.persistence.Column;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ColumnOperationTest {

    @Test
    void should_get_column_value_by_java_type() throws NoSuchFieldException {
        Long money = 1000_000_0L;
        TestClass instance = new TestClass(money);

        DatabaseColumn column = DatabaseColumn.fromField(TestClass.class.getDeclaredField("money"), instance);

        assertThat(column.getColumnValueByJavaType()).isEqualTo(money);
    }


    private class TestClass {

        @Column
        private Long money;

        public TestClass(Long money) {
            this.money = money;
        }
    }
}
