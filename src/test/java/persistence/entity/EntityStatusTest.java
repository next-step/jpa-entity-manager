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


    @Test
    @DisplayName("READ ONLY 상태인지 확인한다.")
    void isManged() {
        //given
        EntityStatus entityStatus = EntityStatus.MANAGED;

        //when
        final boolean managed = entityStatus.isManaged();

        //then
        assertTrue(managed);
    }

    @Test
    @DisplayName("LOADING 상태인지 확인한다.")
    void isLoading() {
        //given
        EntityStatus entityStatus = EntityStatus.LOADING;

        //when
        final boolean loading = entityStatus.isLoading();

        //then
        assertTrue(loading);
    }

    @Test
    @DisplayName("DELETED 상태인지 확인한다.")
    void isDeleted() {
        //given
        EntityStatus entityStatus = EntityStatus.DELETED;

        //when
        final boolean deleted = entityStatus.isDeleted();

        //then
        assertTrue(deleted);
    }

}
