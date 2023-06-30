package queenofkelp.simplewarfare.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import queenofkelp.simplewarfare.gun.item.Gun;
import queenofkelp.simplewarfare.util.gun.GunShooterUtil;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity {
    public AbstractClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    long lastAdsTick;

    @Inject(method = "getFovMultiplier", at = @At("HEAD"), cancellable = true)
    public void getFovMultiplier(CallbackInfoReturnable<Float> cir) {
        ItemStack gunStack = this.getMainHandStack();

        if (GunShooterUtil.isPlayerADSing(this)) {
            lastAdsTick = this.getWorld().getTime();
        }
        if (this.getWorld().getTime() <= lastAdsTick + 3 && gunStack.getItem() instanceof Gun gun) {
            cir.setReturnValue(MathHelper.lerp((MinecraftClient.getInstance().options.getFovEffectScale().getValue()).floatValue(), 1.0F, gun.getAdsFovMult(gunStack)));
        }
    }
}
