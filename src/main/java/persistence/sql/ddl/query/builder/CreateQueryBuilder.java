package persistence.sql.ddl.query.builder;

import persistence.sql.dialect.database.ConstraintsMapper;
import persistence.sql.dialect.database.TypeMapper;
import persistence.sql.entity.EntityMappingTable;
import persistence.sql.entity.model.DomainType;
import persistence.sql.entity.model.DomainTypes;
import persistence.sql.entity.model.TableName;

import java.util.List;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static persistence.sql.constant.SqlConstant.COMMA;
import static persistence.sql.constant.SqlFormat.CREATE;

public class CreateQueryBuilder {

    private final TableName tableName;
    private final List<ColumnBuilder> columnBuilders;

    private CreateQueryBuilder(final TableName tableName,
                               final List<ColumnBuilder> columnBuilders) {
        this.tableName = tableName;
        this.columnBuilders = columnBuilders;
    }

    public static CreateQueryBuilder of(final EntityMappingTable entityMappingTable,
                                        final TypeMapper typeMapper,
                                        final ConstraintsMapper constantTypeMapper) {
        List<ColumnBuilder> columnBuilders = getColumnBuilders(
                entityMappingTable.getDomainTypes(),
                typeMapper,
                constantTypeMapper);
        return new CreateQueryBuilder(entityMappingTable.getTableName(), columnBuilders);
    }

    private static List<ColumnBuilder> getColumnBuilders(final DomainTypes domainTypes,
                                                         final TypeMapper typeMapper,
                                                         final ConstraintsMapper constantTypeMapper) {
        Spliterator<DomainType> spliterator = domainTypes.spliterator();
        return StreamSupport.stream(spliterator, false)
                .filter(DomainType::isEntityColumn)
                .map(domainType -> new ColumnBuilder(domainType, typeMapper, constantTypeMapper))
                .collect(Collectors.toList());
    }

    public String toSql() {
        String columns = columnBuilders.stream()
                .map(ColumnBuilder::build)
                .collect(Collectors.joining(COMMA.getValue()));

        return String.format(CREATE.getFormat(), tableName.getName(), columns);
    }

}
