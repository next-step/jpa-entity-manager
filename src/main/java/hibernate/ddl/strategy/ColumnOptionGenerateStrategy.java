package hibernate.ddl.strategy;

import hibernate.entity.meta.column.EntityColumn;

public interface ColumnOptionGenerateStrategy {

    boolean acceptable(EntityColumn entityColumn);

    String generateColumnOption();
}
