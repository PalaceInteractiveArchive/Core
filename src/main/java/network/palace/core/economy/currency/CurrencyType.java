package network.palace.core.economy.currency;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@AllArgsConstructor
public enum CurrencyType {
    BALANCE("$", ChatColor.GREEN), TOKENS("✪ ", ChatColor.YELLOW), ADVENTURE("➶", ChatColor.GREEN);

    @Getter private String icon;
    @Getter private ChatColor color;

    public String getName() {
        return name().toLowerCase();
    }

    public static CurrencyType fromString(String s) {
        switch (s.toLowerCase()) {
            case "balance":
                return BALANCE;
            case "tokens":
                return TOKENS;
            case "adventure":
                return ADVENTURE;
            default:
                return null;
        }
    }

}
