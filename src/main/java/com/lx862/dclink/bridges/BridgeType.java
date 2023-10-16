package com.lx862.dclink.bridges;

public enum BridgeType {
    DISCORD("Discord"),
    REVOLT("Revolt");

    private final String name;
    BridgeType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
