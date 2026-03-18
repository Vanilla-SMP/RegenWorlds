package com.regenworlds.model;

import org.bukkit.World;

public enum VoidWorldType {
    OVERWORLD, NETHER, END;

    public static VoidWorldType fromWorld(World world) {
        return switch (world.getEnvironment()) {
            case NETHER -> NETHER;
            case THE_END -> END;
            default -> OVERWORLD;
        };
    }

    public World.Environment toEnvironment() {
        return switch (this) {
            case NETHER -> World.Environment.NETHER;
            case END -> World.Environment.THE_END;
            case OVERWORLD -> World.Environment.NORMAL;
        };
    }
}
