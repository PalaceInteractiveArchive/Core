package com.palacemc.core.economy;

import java.util.UUID;

/**
 * Created by Marc on 8/20/15
 */
public class Payment {
    private UUID uuid;
    private int amount;
    private String source;

    public Payment(UUID uuid, int amount, String source) {
        this.uuid = uuid;
        this.amount = amount;
        this.source = source;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public int getAmount() {
        return amount;
    }

    public String getSource() {
        return source;
    }
}