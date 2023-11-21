package persistence;

import persistence.sql.ddl.DataDefinitionLanguageGenerator;
import persistence.sql.ddl.assembler.DataDefinitionLanguageAssembler;
import persistence.sql.dialect.H2Dialect;
import persistence.sql.dml.DataManipulationLanguageGenerator;
import persistence.sql.dml.assembler.DataManipulationLanguageAssembler;
import persistence.sql.usecase.GetFieldFromClass;
import persistence.sql.usecase.GetFieldValue;
import persistence.sql.usecase.GetIdDatabaseField;
import persistence.sql.usecase.GetTableNameFromClass;

public class TestUtils {
    public static DataManipulationLanguageAssembler createDataManipulationLanguageAssembler() {
        H2Dialect h2Dialect = new H2Dialect();
        GetTableNameFromClass getTableNameFromClass = new GetTableNameFromClass();
        GetFieldFromClass getFieldFromClass = new GetFieldFromClass();
        GetFieldValue getFieldValue = new GetFieldValue();
        GetIdDatabaseField getIdDatabaseField = new GetIdDatabaseField(getFieldFromClass);
        DataManipulationLanguageGenerator dataManipulationLanguageGenerator = new DataManipulationLanguageGenerator(
            getTableNameFromClass,
            getFieldFromClass,
            getFieldValue,
            getIdDatabaseField);
        return new DataManipulationLanguageAssembler(
            h2Dialect, dataManipulationLanguageGenerator
        );
    }

    public static DataDefinitionLanguageAssembler createDataDefinitionLanguageAssembler() {
        GetTableNameFromClass getTableNameFromClass = new GetTableNameFromClass();
        GetFieldFromClass getFieldFromClass = new GetFieldFromClass();
        DataDefinitionLanguageGenerator dataDefinitionLanguageGenerator = new DataDefinitionLanguageGenerator(
            getTableNameFromClass, getFieldFromClass
        );
        return new DataDefinitionLanguageAssembler(dataDefinitionLanguageGenerator);
    }
}
