package persistence.sql.ddl.query.builder;

import persistence.sql.dialect.database.ConstraintsMapper;
import persistence.sql.dialect.database.TypeMapper;
import persistence.sql.entity.model.Constraints;
import persistence.sql.entity.model.DomainType;
import persistence.sql.entity.model.NormalDomainType;
import persistence.sql.entity.model.PrimaryDomainType;

import static persistence.sql.constant.SqlConstant.BLANK;
import static persistence.sql.constant.SqlConstant.EMPTY;

public class ColumnBuilder {

    private final DomainType domainType;
    private final TypeMapper typeMapper;
    private final ConstraintsMapper constantTypeMapper;

    public ColumnBuilder(final DomainType domainType,
                         final TypeMapper typeMapper,
                         final ConstraintsMapper constantTypeMapper) {
        this.domainType = domainType;
        this.typeMapper = typeMapper;
        this.constantTypeMapper = constantTypeMapper;
    }

    public String build() {
        return String.join(BLANK.getValue(),
                getColumnName(),
                getColumnType(),
                getPkConstantType(),
                getConstantType()
        ).trim();
    }

    private String getColumnName() {
        return domainType.getColumnName();
    }

    private String getColumnType() {
        return typeMapper.toSqlType(domainType.getClassType());
    }

    private String getPkConstantType() {
        if (!domainType.isPrimaryDomain()) {
            return EMPTY.getValue();
        }

        final PrimaryDomainType primaryDomainType = (PrimaryDomainType) domainType;

        return primaryDomainType.isIdentityGenerationType() ?
                constantTypeMapper.getConstantType(Constraints.PK) :
                constantTypeMapper.getConstantType(Constraints.PRIMARY_KEY);
    }

    private String getConstantType() {
        if (domainType.isPrimaryDomain()) {
            return EMPTY.getValue();
        }

        final NormalDomainType normalDomainType = (NormalDomainType) domainType;

        return normalDomainType.isNotNull() ?
                constantTypeMapper.getConstantType(Constraints.NOT_NULL) :
                EMPTY.getValue();
    }

}
