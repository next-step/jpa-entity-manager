package persistence.sql.transaction;

public class TransactionStatus {
    private boolean active;

    public TransactionStatus() {
        this.active = false;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
