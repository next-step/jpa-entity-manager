package persistence.sql.entity.model;

import jakarta.persistence.Id;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class DomainTypes implements Iterable<DomainType> {

    private final List<DomainType> domainTypes;

    private DomainTypes(final List<DomainType> domainTypes) {
        this.domainTypes = domainTypes;
    }

    public static DomainTypes from(final Field[] fields) {
        return new DomainTypes(Arrays.stream(fields)
                .map(field -> field.isAnnotationPresent(Id.class) ?
                        PrimaryDomainType.ofPrimaryDomainType(field, null) :
                        NormalDomainType.of(field, null))
                .collect(Collectors.toList()));
    }

    public static DomainTypes of(final Field[] fields,
                                 final Object entity) {
        return new DomainTypes(Arrays.stream(fields)
                .map(field -> field.isAnnotationPresent(Id.class) ?
                        PrimaryDomainType.ofPrimaryDomainType(field, entity) :
                        NormalDomainType.of(field, entity))
                .collect(Collectors.toList()));
    }

    public List<DomainType> getDomainTypes() {
        return domainTypes;
    }

    public List<String> getColumnName() {
        return this.getDomainTypes()
                .stream()
                .filter(DomainType::isEntityColumn)
                .map(DomainType::getColumnName)
                .collect(Collectors.toList());
    }

    @Override
    public Iterator<DomainType> iterator() {
        return domainTypes.iterator();
    }
}
