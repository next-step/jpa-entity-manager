package persistence.core;

public class EntityKey {
    private String key;

    public EntityKey(Class<?> clazz, Long id) {
        this.key = genEntityKey(clazz.getSimpleName(), id);
    }

    public static String genEntityKey(String entityName, Long id) {
        return entityName + ":" + id;
    }

    public String getKey() {
        return key;
    }

    public static String from(Class<?> clazz, Long id) {
        return genEntityKey(clazz.getSimpleName(), id);
    }
}
