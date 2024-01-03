package com.lx862.vendorneutral.command;

import java.util.function.Consumer;

public class Command {
    public final String commandName;
    public final String description;
    private final Consumer<String> callback;

    public Command(String commandName, String description, Consumer<String> callback) {
        this.commandName = commandName;
        this.description = description;
        this.callback = callback;
    }

    public void execute(String content) {
        callback.accept(content);
    }
}
