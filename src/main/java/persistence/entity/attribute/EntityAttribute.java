package persistence.entity.attribute;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import persistence.entity.attribute.id.IdAttribute;
import persistence.entity.attribute.id.resolver.*;
import persistence.entity.attribute.resolver.*;
import persistence.sql.ddl.wrapper.DDLWrapper;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EntityAttribute {
    private final String tableName;
    private final List<GeneralAttribute> generalAttributes;
    private final IdAttribute idAttribute;
    private static final List<IdAttributeResolver> ID_ATTRIBUTE_RESOLVERS;

    private static final List<IdAttributeResolver> GENERAL_ATTRIBUTE_RESOLVERS;

    static {
        ID_RESOLVERS = Arrays.asList(
                new StringTypeGeneralAttributeResolver(),
                new LongTypeGeneralAttributeResolver(),
                new IntegerTypeGeneralAttributeResolver()
        );
    }

    static {
        ID_RESOLVERS = Arrays.asList(
                new StringTypeIdAttributeResolver(),
                new LongTypeIdAttributeResolver()
        );
    }

    private EntityAttribute(
            String tableName,
            IdAttribute idAttribute,
            List<GeneralAttribute> generalAttributes
    ) {
        this.tableName = tableName;
        this.generalAttributes = generalAttributes;
        this.idAttribute = idAttribute;
    }

    public static EntityAttribute of(Class<?> clazz) {
        validate(clazz);

        String tableName = Optional.ofNullable(clazz.getAnnotation(Table.class)).map(Table::name).orElse(clazz.getSimpleName());

        List<GeneralAttribute> generalAttributes = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Column.class)
                        && !field.isAnnotationPresent(Id.class))
                .map(it -> {
                    for (GeneralAttributeResolver generalAttributeResolver : GENERAL_ATTRIBUTE_RESOLVERS) {
                        if (generalAttributeResolver.support(it.getType())) {
                            return generalAttributeResolver.resolver(it)
                        }
                    })
                }).collect(Collectors.toList());


        Field idField = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("[%s] 엔티티에 @Id가 없습니다", clazz.getSimpleName())));

        IdAttribute idAttribute = null;

        for (IdAttributeResolver idAttributeResolver : ID_RESOLVERS) {
            if (idAttributeResolver.supports(idField.getType())) {
                idAttribute = idAttributeResolver.resolve(idField);
            }
        }

        assert idAttribute != null;

        return new EntityAttribute(tableName, idAttribute, generalAttributes);
    }

    private static void validate(Class<?> clazz) {
        long idAnnotatedFieldCount = Arrays.stream((clazz.getDeclaredFields()))
                .filter(it -> it.isAnnotationPresent(Id.class))
                .count();

        if (idAnnotatedFieldCount != 1) {
            throw new IllegalStateException(String.format("[%s] @Id 어노테이션이 1개가 아닙니다.", clazz.getSimpleName()));
        }
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalStateException(String.format("[%s] @Entity 어노테이션이 없습니다.", clazz.getSimpleName()));
        }
    }

    public String prepareDDL(DDLWrapper ddlWrapper) {
        return ddlWrapper.wrap(tableName, idAttribute, generalAttributes);
    }

    public String getTableName() {
        return tableName;
    }

    public List<GeneralAttribute> getGeneralAttributes() {
        return generalAttributes;
    }

    public IdAttribute getIdAttribute() {
        return idAttribute;
    }
}
