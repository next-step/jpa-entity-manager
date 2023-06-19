package persistence.dialect;

public interface IdentifyColumnDialect {

    String identifyColumnString();

    String primaryKeyString(String name);
}
