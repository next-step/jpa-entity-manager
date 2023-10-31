package domain;

import persistence.sql.common.instance.Values;

public class Snapshot {
    private Object id;
    private Object object;
    private Values values;

    public Snapshot(Object id, Object object) {
        this.id = id;
        this.object = object;
        this.values = Values.of(object);
    }

    public Object getId() {
        return id;
    }

    public Object getObject() {
        return object;
    }

    public Class getObjectClass() {
        return object.getClass();
    }

    public Values getValues() {
        return values;
    }
}
