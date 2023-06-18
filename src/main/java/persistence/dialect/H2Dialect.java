package persistence.dialect;

public class H2Dialect extends Dialect {
    public static final H2IdentifyColumnDialect IDENTIFY_INSTANCE = new H2IdentifyColumnDialect();

    @Override
    String getNativeIdentifierGeneratorStrategy() {
        return IDENTIFY_INSTANCE.identifyColumnString();
    }
}
