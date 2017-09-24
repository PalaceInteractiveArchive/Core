package network.palace.core.economy;

public enum CurrencyType {
    BALANCE, TOKENS;

    public String getName() {
        return name().toLowerCase();
    }

    public static CurrencyType fromString(String s) {
        switch (s.toLowerCase()) {
            case "balance":
                return BALANCE;
            case "tokens":
                return TOKENS;
            default:
                return BALANCE;
        }
    }

}
