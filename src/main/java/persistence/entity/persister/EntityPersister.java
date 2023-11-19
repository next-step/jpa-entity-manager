package persistence.entity.persister;

import jdbc.JdbcTemplate;
import lombok.extern.slf4j.Slf4j;
import persistence.sql.dml.assembler.DataManipulationLanguageAssembler;

@Slf4j
public class EntityPersister {
    private final DataManipulationLanguageAssembler dataManipulationLanguageAssembler;
    private final JdbcTemplate jdbcTemplate;


    public EntityPersister(DataManipulationLanguageAssembler dataManipulationLanguageAssembler, JdbcTemplate jdbcTemplate) {
        this.dataManipulationLanguageAssembler = dataManipulationLanguageAssembler;
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean update(Object object) {
        String sql = dataManipulationLanguageAssembler.generateUpdate(object);
        jdbcTemplate.execute(sql);
        return true;
    }

    public Long insert(Object object) {
        String sql = dataManipulationLanguageAssembler.generateInsert(object);
        return jdbcTemplate.insertSingle(sql);
    }

    public void delete(Object object) {
        String sql = dataManipulationLanguageAssembler.generateDeleteWithWhere(object);
        jdbcTemplate.execute(sql);
    }
}
