package persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.Person;
import persistence.sql.dialect.MysqlDialect;

import static org.assertj.core.api.Assertions.assertThat;

class EntityMetaDataTest {

    @DisplayName("EntityMetaData의 정보가 다르면 true를 반환한다.")
    @Test
    void isDirtyWhenDifferentData(){
        Person person = new Person("KIM", "kim@test.com", 30);
        Person person1 = new Person("LEE", "kim@test.com", 20);

        EntityMetaData entityMetaData = new EntityMetaData(person, new MysqlDialect());
        EntityMetaData entityMetaData2 = new EntityMetaData(person1, new MysqlDialect());

        assertThat(entityMetaData.isDirty(entityMetaData2)).isTrue();
    }

    @DisplayName("EntityMetaData의 정보가 같으면 false를 반환한다.")
    @Test
    void isDirtyWhenSameData(){
        Person person = new Person("KIM", "kim@test.com", 30);

        EntityMetaData entityMetaData = new EntityMetaData(person, new MysqlDialect());
        EntityMetaData entityMetaData2 = new EntityMetaData(person, new MysqlDialect());

        assertThat(entityMetaData.isDirty(entityMetaData2)).isFalse();
    }

}
