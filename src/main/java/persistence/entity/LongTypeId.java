package persistence.entity;

public class LongTypeId {

    private final Object entity;

    public LongTypeId(Object entity) {
        this.entity = entity;
    }

    public Long getId() {
        try {
            return (Long) entity.getClass().getMethod("getId").invoke(entity);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isEntityIdNull() {
        return getId() == null;
    }

}
