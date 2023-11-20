package persistence.sql.dml;

import fixture.PersonFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UpdateQueryBuilderTest {

    @DisplayName("update 쿼리 생성 확인")
    @Test
    void getQuery() {
        //when
        String query = UpdateQueryBuilder.getQuery(PersonFixture.changgunyee());

        //then
        assertThat(query).isEqualTo("UPDATE SET nick_name='changgunyee', " +
                "old=29, " +
                "email='minyoung403@naver.com' " +
                "WHERE id=1;");
    }
}