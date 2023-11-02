package persistence.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EntityStatusTest {

    @Test
    @DisplayName("Gone 상태인지 확인한다.")
    void isGone() {
        //given
        EntityStatus entityStatus = EntityStatus.GONE;

        //when
        final boolean gone = entityStatus.isGone();

        //then
        assertTrue(gone);
    }

    @Test
    @DisplayName("READ ONLY 상태인지 확인한다.")
    void isReadOnly() {
        //given
        EntityStatus entityStatus = EntityStatus.READ_ONLY;

        //when
        final boolean readOnly = entityStatus.isReadOnly();

        //then
        assertTrue(readOnly);
    }


}
