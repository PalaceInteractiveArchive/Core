package network.palace.core.economy;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum CurrencyType {
    BALANCE("$"), TOKENS("✪ ");

    @Getter private String icon;

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
