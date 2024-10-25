package persistence.sql.event;

@FunctionalInterface
public interface FlushEventListener {
    void onFlush();
}
