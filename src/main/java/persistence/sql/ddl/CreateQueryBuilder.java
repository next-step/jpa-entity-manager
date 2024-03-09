package persistence.sql.ddl;

import domain.dialect.Dialect;
import domain.pojo.EntityMetaData;
import domain.pojo.FieldInfo;
import domain.pojo.FieldInfos;
import domain.pojo.FieldName;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static domain.constants.CommonConstants.COMMA;
import static domain.constants.CommonConstants.SPACE;
import static domain.pojo.Constraints.NOT_NULL;
import static domain.pojo.Constraints.PRIMARY_KEY;

public class CreateQueryBuilder {

    private static final String CREATE_TABLE_QUERY = "CREATE TABLE %s ( %s );";

    private final Dialect dialect;
    private final EntityMetaData entityMetaData;

    public CreateQueryBuilder(Dialect dialect, EntityMetaData entityMetaData) {
        this.dialect = dialect;
        this.entityMetaData = entityMetaData;
    }

    public String createTable(Object object) {
        FieldInfos fieldInfos = new FieldInfos(object.getClass().getDeclaredFields());
        return String.format(CREATE_TABLE_QUERY, entityMetaData.getTableInfo().getName(), createClause(fieldInfos.getFieldDataList()));
    }

    private String createClause(List<FieldInfo> fieldInfoList) {
        return fieldInfoList.stream()
                .map(this::getFieldInfo)
                .collect(Collectors.joining(COMMA))
                .replaceAll(",[\\s,]*$", "");
    }

    private String getFieldInfo(FieldInfo fieldInfo) {
        Field field = fieldInfo.getField();
        String fieldName = new FieldName(field).getName();
        boolean varcharType = dialect.isVarcharType(field.getType());

        if (fieldInfo.isIdField()) {
            return Stream.of(fieldName, dialect.getTypeToStr(field.getType()), NOT_NULL.getName(),
                            PRIMARY_KEY.getName(), fieldInfo.getGenerationTypeStrategy())
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(SPACE));
        }

        return Stream.of(fieldName, dialect.getTypeToStr(field.getType()),
                        fieldInfo.getFieldLength(varcharType), fieldInfo.getColumnNullConstraint())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(SPACE));
    }
}
