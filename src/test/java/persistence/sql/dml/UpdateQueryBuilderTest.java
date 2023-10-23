package persistence.sql.dml;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.exception.PersistenceException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UpdateQueryBuilderTest {
    @Test
    @DisplayName("주어진 data 를 이용해 Update Query 을 생성 할 수 있다.")
    void buildTest() {
        final String query = UpdateQueryBuilder.builder()
                .table("test")
                .addData("id", "1")
                .addData("test", "test")
                .where("id", "1")
                .build();

        assertThat(query).isEqualToIgnoringCase("update test set id=1, test=test where id=1");
    }

    @Test
    @DisplayName("TableName 없이 build 할 수 없다.")
    void noTableNameBuildFailureTest() {
        assertThatThrownBy(() -> UpdateQueryBuilder.builder()
                .addData("test", "test")
                .build())
                .isInstanceOf(PersistenceException.class);
    }

    @Test
    @DisplayName("주어진 data 없이 build 할 수 없다.")
    void noDataBuildFailureTest() {
        assertThatThrownBy(() -> UpdateQueryBuilder.builder()
                .table("test")
                .build())
                .isInstanceOf(PersistenceException.class);
    }

    @Test
    @DisplayName("data 를 한꺼번에 넣을땐 columns 와 values 길이가 같아야한다.")
    void columnsValuesSizeNotSameTest() {
        assertThatThrownBy(() -> UpdateQueryBuilder.builder()
                .addData(List.of("test"), List.of("1", "2"))
                .build())
                .isInstanceOf(PersistenceException.class);
    }
}
