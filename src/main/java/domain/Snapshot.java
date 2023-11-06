package domain;

public class Snapshot {

    private Object id;
    private Object object;

    public Snapshot(Object id, Object object) {
        this.id = id;
        this.object = object;
    }

    public Object getId() {
        return id;
    }

    public Object getObject() {
        return object;
    }
}
