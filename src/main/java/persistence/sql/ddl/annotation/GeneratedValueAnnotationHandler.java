package persistence.sql.ddl.annotation;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import persistence.sql.ddl.ColumnOption;
import persistence.sql.ddl.dialect.Dialect;
import persistence.sql.ddl.scheme.ColumnSchemes;
import persistence.sql.ddl.scheme.Schemes;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class GeneratedValueAnnotationHandler extends AnnotationHandler<GeneratedValue> {

    public GeneratedValueAnnotationHandler(Field field, Dialect dialect) {
        super(field, GeneratedValue.class, dialect);
    }

    @Override
    public List<ColumnOption> metaInfos() {

        List<ColumnOption> result = new ArrayList<>();
        ColumnSchemes schemes = dialect.getSchemes(Schemes.GeneratedValue);

        if (annotation.strategy().name().equals(GenerationType.IDENTITY.name())) {
            ColumnOption columnOption = ColumnOption.valueOf(schemes.getScheme("IDENTITY"));
            result.add(columnOption);
        }

        return result;
    }

}
