package persistence.dialect;

import jakarta.persistence.GenerationType;
import persistence.metadata.ColumnType;

public class H2Dialect implements Dialect{
    @Override
    public String getColumnType(int type) {
        return ColumnType.getColumnTYpe(type);
    }

    @Override
    public String getIdentifierGenerationType(GenerationType type) {
        return ColumnType.getIdentifierGenerationType(type);
    }
}
