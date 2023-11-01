package persistence.mapper;

import fixtures.EntityFixtures;
import jdbc.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TestEntityRowMapper implements RowMapper<List<EntityFixtures.SampleOneWithValidAnnotation>> {
    @Override
    public List<EntityFixtures.SampleOneWithValidAnnotation> mapRow(ResultSet rs) throws SQLException {
        List<EntityFixtures.SampleOneWithValidAnnotation> entities = new ArrayList<>();

        while (rs.next()) {
            entities.add(new EntityFixtures.SampleOneWithValidAnnotation(
                    rs.getLong("ID"),
                    rs.getString("NAME"),
                    rs.getInt("OLD")
            ));
        }

        return entities;
    }
}
