package com.regenworlds.model;

import java.io.Serializable;

public class PlayerReturn implements Serializable {
    public final String worldName;
    public final double x, y, z;
    public final float yaw, pitch;

    public PlayerReturn(String worldName, double x, double y, double z, float yaw, float pitch) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }
}
