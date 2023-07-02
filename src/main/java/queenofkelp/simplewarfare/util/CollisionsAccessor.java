package queenofkelp.simplewarfare.util;

import net.minecraft.util.math.Vec3d;

public interface CollisionsAccessor {
    private Vec3d adjustMovementForCollisions(Vec3d movement) {
        return null;
    }

    public Vec3d adjustMovementForCollisionsPublic(Vec3d movement);
}
