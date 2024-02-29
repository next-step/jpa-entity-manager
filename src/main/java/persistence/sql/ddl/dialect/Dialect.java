package persistence.sql.ddl.dialect;

import persistence.sql.ddl.domain.Constraint;
import persistence.sql.ddl.domain.Type;

public interface Dialect {

    String getTypeString(Type type, int length);

    String getConstraintString(Constraint constraint);

}
