package network.palace.core.economy;

public abstract class TransactionCallback {
    protected void handled(boolean success, String error) {
        try {
            callback(success, error);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void callback(boolean success, String error);
}
