package com.thepalace.core.command;

import lombok.Getter;

public class CommandException extends Exception {

    @Getter private final String message;

    public CommandException(String message) {
        this.message = message;
    }
}
