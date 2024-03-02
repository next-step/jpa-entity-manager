package persistence.sql.entity.model;

public interface DomainType {

    boolean isPrimaryDomain();

    boolean isEntityColumn();

    String getName();

    String getColumnName();

    String getValue();

    Class<?> getClassType();

}
