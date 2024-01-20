package queenofkelp.simplewarfare.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import queenofkelp.simplewarfare.bullet.entity.BulletEntity;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At("HEAD"), cancellable = true)
    private void adjustMovementForCollisions(Vec3d movement, CallbackInfoReturnable<Vec3d> cir) {
        if ((Entity) (Object) this instanceof BulletEntity) {
            cir.setReturnValue(movement);
        }
    }
}
