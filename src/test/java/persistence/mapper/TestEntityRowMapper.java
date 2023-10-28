package persistence.mapper;

import fixtures.TestEntityFixtures;
import jdbc.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TestEntityRowMapper implements RowMapper<List<TestEntityFixtures.SampleOneWithValidAnnotation>> {
    @Override
    public List<TestEntityFixtures.SampleOneWithValidAnnotation> mapRow(ResultSet rs) throws SQLException {
        List<TestEntityFixtures.SampleOneWithValidAnnotation> entities = new ArrayList<>();

        while (rs.next()) {
            entities.add(new TestEntityFixtures.SampleOneWithValidAnnotation(
                    rs.getLong("ID"),
                    rs.getString("NAME"),
                    rs.getInt("OLD")
            ));
        }

        return entities;
    }
}
