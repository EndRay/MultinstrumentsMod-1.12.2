package com.example.instrumentsmod.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3i;

public class PosUtils {
    private PosUtils(){}

    public static Vec3i directionByYaw(float yaw){
        if(yaw <= 45 || yaw > 315)
            return new Vec3i(0, 0, 1);
        else if(yaw > 45 && yaw <= 135)
            return new Vec3i(-1, 0, 0);
        else if(yaw > 135 && yaw <= 225)
            return new Vec3i(0, 0, -1);
        else if(yaw > 225)
            return new Vec3i(1, 0, 0);
        else throw new IllegalArgumentException("Incorrect yaw.");
    }

    public static Vec3i getPlayerDirection(EntityPlayer player){
        return directionByYaw(player.getRotationYawHead());
    }

    public static Vec3i getRotatedShift(Vec3i direction, RelativePos pos){
        return new Vec3i(direction.getX() * pos.front - direction.getZ() * pos.right, pos.up, direction.getZ() * pos.front + direction.getX() * pos.right);
    }
}
