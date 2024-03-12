package persistence.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.PersonV3FixtureFactory;
import persistence.sql.ddl.PersonV0;
import persistence.sql.ddl.PersonV3;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EntityMetaDataTest {

    @DisplayName("메타 데이터에 있는 필드들을 이용해 entity 객체에서 필드 값들을 추출한다")
    @Test
    public void extractValues() throws Exception {
        // given
        final PersonV3 person = PersonV3FixtureFactory.generatePersonV3Stub();
        final EntityMetaData metaData = new EntityMetaData(person.getClass());

        // when
        final Map<String, Object> values = metaData.extractValues(person);

        // then
        assertThat(values)
                .extracting("id", "name", "age", "email")
                .contains(person.getId(), person.getName(), person.getAge(), person.getEmail());
    }

    @DisplayName("메타 데이터로 entity 객체의 필드 값들을 추출할 때, 알맞은 클래스 타입이 아니라면 예외를 반환한다")
    @Test
    public void extractValuesNotEqualClassType() throws Exception {
        // given
        final PersonV3 person = PersonV3FixtureFactory.generatePersonV3Stub();
        final EntityMetaData metaData = new EntityMetaData(PersonV0.class);

        // when then
        assertThatThrownBy(() -> metaData.extractValues(person))
                .isInstanceOf(MetaDataModelMappingException.class)
                .hasMessage("not equal class type - meta data type: " + PersonV0.class.getName() + ", parameter type: " + person.getClass().getName());
    }

}
