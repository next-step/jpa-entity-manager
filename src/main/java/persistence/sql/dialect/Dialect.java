package persistence.sql.dialect;

import jakarta.persistence.GenerationType;
import persistence.sql.ddl.KeyType;

public interface Dialect {
    String mapDataType(int type);
    String mapGenerationType(GenerationType strategy);
    String mapKeyType(KeyType keyType);
    String getGeneratedIdSelectQuery();
}
