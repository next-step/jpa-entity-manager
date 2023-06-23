package persistence.dialect;

public enum Database {
    H2 {
        @Override
        public Dialect createDialect() {
            return new H2Dialect();
        }
    },
    ;

    public abstract Dialect createDialect();
}
