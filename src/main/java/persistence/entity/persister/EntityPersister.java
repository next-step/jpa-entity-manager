package persistence.entity.persister;

import jdbc.JdbcTemplate;
import persistence.sql.dml.assembler.DataManipulationLanguageAssembler;

public class EntityPersister {
    private final DataManipulationLanguageAssembler dataManipulationLanguageAssembler;
    private final JdbcTemplate jdbcTemplate;


    public EntityPersister(DataManipulationLanguageAssembler dataManipulationLanguageAssembler, JdbcTemplate jdbcTemplate) {
        this.dataManipulationLanguageAssembler = dataManipulationLanguageAssembler;
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean update(Object object) {
        jdbcTemplate.execute(dataManipulationLanguageAssembler.generateUpdate(object));
        return true;
    }

    public Long insert(Object object) {
        return jdbcTemplate.insertSingle(dataManipulationLanguageAssembler.generateInsert(object));
    }

    public void delete(Object object) {
        jdbcTemplate.execute(dataManipulationLanguageAssembler.generateDeleteWithWhere(object));
    }
}
