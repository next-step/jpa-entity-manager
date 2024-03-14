package persistence.entity.loader;

import jdbc.JdbcTemplate;
import persistence.entity.exception.NotUniqueDataException;
import persistence.sql.common.DtoMapper;
import persistence.sql.dml.querybuilder.SelectQueryBuilder;

import java.util.List;
import java.util.Optional;

public class EntityLoader {
    public static final int VALID_ROW_COUNT_FOR_FIND_METHOD = 1;
    public static final int VALID_ROW_INDEX = 0;
    private final JdbcTemplate jdbcTemplate;

    public EntityLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T> Optional<T> find(Class<T> clazz, Long id) {
        String query = new SelectQueryBuilder(clazz).getFindById(id);
        List<T> selected = jdbcTemplate.query(query, new DtoMapper<>(clazz));
        return getFoundResult(selected, id);
    }

    private <T> Optional<T> getFoundResult(List<T> selected, Long id) {
        if (selected.isEmpty()) {
            return Optional.empty();
        }
        if (selected.size() > VALID_ROW_COUNT_FOR_FIND_METHOD) {
            throw new NotUniqueDataException(id);
        }
        return Optional.of(selected.get(VALID_ROW_INDEX));
    }
}
