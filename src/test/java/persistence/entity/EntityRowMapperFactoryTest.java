package persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;
import jdbc.RowMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import persistence.sql.ddl.entity.Person;

class EntityRowMapperFactoryTest {

    @DisplayName("요구사항 1 - RowMapper 리팩터링")
    @ParameterizedTest(name = "id: {0}, name: {1}, age: {2}, email: {3}")
    @CsvSource(
        value = {
            "1,user1,30,test@gmail.com"
        }
    )
    void getRowMapper(Long givenId, String givenName, Integer givenAge, String givenEmail) throws SQLException {
        // given
        ResultSet mockedResultSet = mock(ResultSet.class);

        RowMapper<Person> rowMapper = EntityRowMapperFactory.getInstance()
            .getRowMapper(Person.class);

        when(mockedResultSet.getObject("id")).thenReturn(givenId);
        when(mockedResultSet.getObject("nick_name")).thenReturn(givenName);
        when(mockedResultSet.getObject("old")).thenReturn(givenAge);
        when(mockedResultSet.getObject("email")).thenReturn(givenEmail);

        // when
        Person person = rowMapper.mapRow(mockedResultSet);

        // then
        Person expectedPerson = new Person(givenId, givenName, givenAge, givenEmail);
        assertAll(
            () -> assertThat(person).isNotNull(),
            () -> assertThat(person).isEqualTo(expectedPerson)
        );
    }
}
