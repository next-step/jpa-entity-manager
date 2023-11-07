package persistence.entity.persister;

import java.util.HashMap;
import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import persistence.sql.dml.assembler.DataManipulationLanguageAssembler;

public class EntityPersister {
    private final RowMapper<?> rowMapper;
    private final DataManipulationLanguageAssembler dataManipulationLanguageAssembler;
    private final JdbcTemplate jdbcTemplate;


    public EntityPersister(RowMapper<?> rowMapper, DataManipulationLanguageAssembler dataManipulationLanguageAssembler, JdbcTemplate jdbcTemplate) {
        this.rowMapper = rowMapper;
        this.dataManipulationLanguageAssembler = dataManipulationLanguageAssembler;
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean update(Object object) {
        jdbcTemplate.execute(dataManipulationLanguageAssembler.generateUpdate(object));
        return true;
    }

    public void insert(Object object) {
        jdbcTemplate.execute(dataManipulationLanguageAssembler.generateInsert(object));
    }

    public void delete(Object object) {
        jdbcTemplate.execute(dataManipulationLanguageAssembler.generateDeleteWithWhere(object));
    }
}
