package com.lx862.revoltapi.data;

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
