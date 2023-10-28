package persistence.entity.attribute;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import persistence.entity.attribute.id.IdAttribute;
import persistence.entity.attribute.resolver.GeneralAttributeResolver;
import persistence.sql.ddl.wrapper.DDLWrapper;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static persistence.entity.attribute.resolver.AttributeHolder.GENERAL_ATTRIBUTE_RESOLVERS;
import static persistence.entity.attribute.resolver.AttributeHolder.ID_ATTRIBUTE_RESOLVERS;

public class EntityAttribute {
    private final String tableName;
    private final List<GeneralAttribute> generalAttributes;
    private final IdAttribute idAttribute;

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
        String tableName = Optional.ofNullable(clazz.getAnnotation(Table.class)).map(Table::name).orElse(clazz.getSimpleName());

        List<GeneralAttribute> generalAttributes = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Column.class)
                        && !field.isAnnotationPresent(Id.class))
                .map(it -> {
                    for (GeneralAttributeResolver generalAttributeResolver : GENERAL_ATTRIBUTE_RESOLVERS) {
                        if (generalAttributeResolver.supports(it.getType())) {
                            return generalAttributeResolver.resolve(it);
                        }
                    }
                    throw new RuntimeException("일반 어트리뷰트 파싱 실패 예외");
                }).collect(Collectors.toList());

        Field idField = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("[%s] 엔티티에 @Id가 없습니다", clazz.getSimpleName())));


        IdAttribute idAttribute = ID_ATTRIBUTE_RESOLVERS.stream()
                .filter(resolver -> resolver.supports(idField.getType()))
                .map(resolver -> resolver.resolve(idField))
                .findFirst().orElseThrow(() -> new IllegalStateException("IdAttribute 파싱에 실패했습니다."));

        validate(clazz, idAttribute);

        return new EntityAttribute(tableName, idAttribute, generalAttributes);
    }

    private static void validate(Class<?> clazz, IdAttribute idAttribute) {
        long idAnnotatedFieldCount = Arrays.stream((clazz.getDeclaredFields()))
                .filter(it -> it.isAnnotationPresent(Id.class))
                .count();

        if (idAnnotatedFieldCount != 1) {
            throw new IllegalStateException(String.format("[%s] @Id 어노테이션이 1개가 아닙니다.", clazz.getSimpleName()));
        }
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalStateException(String.format("[%s] @Entity 어노테이션이 없습니다.", clazz.getSimpleName()));
        }
        if (idAttribute == null) {
            throw new IllegalStateException("IdAttribute parse 실패");
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
