package persistence.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static persistence.entity.EntityQueryType.*;

class EntityQueryCacheTest {
    
    private EntityQueryCache<Object> queryCache;

    @BeforeEach
    void setUp() {
        queryCache = new EntityQueryCache<>();
    }

    @Test
    @DisplayName("queryCache 를 이용해 각 쿼리타입마다의 캐싱된 String 을 가져올 수 있다.")
    void entityQueryCacheTest() {
        final String selectQueryResultWithKey1V1 = queryCache.computeIfAbsent(SELECT, 1L, object -> "testSelect1");
        final String selectQueryResultWithKey1V2 = queryCache.computeIfAbsent(SELECT, 1L, object -> "testSelect2");
        final String insertQueryResultWithKey1 = queryCache.computeIfAbsent(INSERT, 1L, object -> "testInsert");
        final String updateQueryResultWithKey1 = queryCache.computeIfAbsent(UPDATE, 1L, object -> "testUpdate");
        final String deleteQueryResultWithKey1 = queryCache.computeIfAbsent(DELETE, 1L, object -> "testDelete");

        assertSoftly(softly -> {
            softly.assertThat(selectQueryResultWithKey1V1).isEqualTo("testSelect1");
            softly.assertThat(selectQueryResultWithKey1V1 == selectQueryResultWithKey1V2).isTrue();
            softly.assertThat(insertQueryResultWithKey1).isEqualTo("testInsert");
            softly.assertThat(updateQueryResultWithKey1).isEqualTo("testUpdate");
            softly.assertThat(deleteQueryResultWithKey1).isEqualTo("testDelete");
        });



    }
}
