package com.thepalace.core.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class CommandException extends Exception {

    @Getter private final String message;

}
