package hibernate.entity;

import jakarta.persistence.Table;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EntityTableNameTest {

    @Test
    void Table_어노테이션의_name이_있으면_name을_받는다() {
        String actual = new EntityTableName(TableEntity.class).getTableName();
        assertThat(actual).isEqualTo("new_table");
    }

    @Test
    void Table_어노테이션의_name이_없으면_클래스명을_받는다() {
        String actual = new EntityTableName(EmptyTableEntity.class).getTableName();
        assertThat(actual).isEqualTo("EmptyTableEntity");
    }

    @Test
    void Table_어노테이션이_없으면_클래스명을_받는다() {
        String actual = new EntityTableName(NoTableEntity.class).getTableName();
        assertThat(actual).isEqualTo("NoTableEntity");
    }

    @Table(name = "new_table")
    static class TableEntity {
    }

    @Table
    static class EmptyTableEntity {
    }

    static class NoTableEntity {
    }
}
