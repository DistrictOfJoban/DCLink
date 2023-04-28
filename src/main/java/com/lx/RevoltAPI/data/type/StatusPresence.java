package com.lx.RevoltAPI.data.type;

public enum StatusPresence {
    ONLINE("Online"),
    IDLE("Idle"),
    FOCUS("Focus"),
    BUSY("Busy"),
    INVISIBLE("Invisible");

    private String name;
    StatusPresence(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
