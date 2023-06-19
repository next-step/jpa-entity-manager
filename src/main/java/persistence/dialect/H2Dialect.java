package persistence.dialect;

public class H2Dialect extends Dialect {
    public static final H2IdentifyColumnDialect IDENTIFY_INSTANCE = new H2IdentifyColumnDialect();

    @Override
    public String getNativeIdentifierGeneratorStrategy() {
        return IDENTIFY_INSTANCE.identifyColumnString();
    }

    @Override
    public String getPrimaryKeyStrategy(String name) {
        return IDENTIFY_INSTANCE.primaryKeyString(name);
    }
}
