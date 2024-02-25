package util.persistence.sql.dialect.identity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.dialect.identity.H2IdentityColumnSupport;
import persistence.sql.dialect.identity.IdentityColumnSupport;

import static org.assertj.core.api.Assertions.assertThat;

class H2IdentityColumnSupportTest {

    private final IdentityColumnSupport identityColumnSupport = new H2IdentityColumnSupport();

    @DisplayName("H2 DB 의 자동 생성 예약어를 반환한다")
    @Test
    public void createToH2IdentityColumnString() throws Exception {
        // when
        final String identityColumnString = identityColumnSupport.getIdentityColumnString();

        // then
        assertThat(identityColumnString).isEqualTo("generated by default as identity");
    }

    @DisplayName("H2 DB 의 insert 쿼리에서 사용되는 identity 컬럼 키워드를 반환한다")
    @Test
    public void createToH2IdentityInsertString() throws Exception {
        // when
        final String identityInsertString = identityColumnSupport.getIdentityInsertString();

        // then
        assertThat(identityInsertString).isEqualTo("default");
    }

    @DisplayName("H2 DB 의 insert 쿼리에서 사용되는 identity 컬럼 키워드의 존재 여부를 반환한다")
    @Test
    public void hasIdentityInsertKeyword() throws Exception {
        // when
        final boolean hasIdentityInsertKeyword = identityColumnSupport.hasIdentityInsertKeyword();

        // then
        assertThat(hasIdentityInsertKeyword).isTrue();
    }

}
