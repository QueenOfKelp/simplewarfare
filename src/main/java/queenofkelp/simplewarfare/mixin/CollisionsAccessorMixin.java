package queenofkelp.simplewarfare.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import queenofkelp.simplewarfare.util.CollisionsAccessor;

@Mixin(Entity.class)
public abstract class CollisionsAccessorMixin implements CollisionsAccessor {
    @Shadow protected abstract Vec3d adjustMovementForCollisions(Vec3d movement);

    public Vec3d adjustMovementForCollisionsPublic(Vec3d movement) {
        return adjustMovementForCollisions(movement);
    };

}
