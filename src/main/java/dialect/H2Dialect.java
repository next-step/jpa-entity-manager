package dialect;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * H2 라는 방언을 결정하는 곳
 */
public class H2Dialect extends Dialect {

    private final Map<Integer, String> map = new HashMap<>();

    public H2Dialect() {
        registerColumnType(Types.INTEGER, Integer.class);
        registerColumnType(Types.BIGINT, Long.class);
        registerColumnType(Types.VARCHAR, String.class);

        map.put(Types.INTEGER, "int");
        map.put(Types.BIGINT, "bigint");
        map.put(Types.VARCHAR, "varchar");
    }

    @Override
    public String getTypeToStr(Class<?> clazz) {
        return map.get(getJavaTypeByClass(clazz));
    }

    @Override
    public void checkJavaType(Integer typeCode) {
        if (!super.containsJavaType(typeCode)) {
            throw new IllegalStateException("지원하는 javaType 이 아닙니다.");
        }
    }
}
