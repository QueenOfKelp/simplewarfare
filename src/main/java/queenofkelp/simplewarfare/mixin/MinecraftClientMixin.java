package queenofkelp.simplewarfare.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import queenofkelp.simplewarfare.gun.item.Gun;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow @Nullable public ClientPlayerEntity player;

    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    public void doAttack(CallbackInfoReturnable<Boolean> cir) {
        if (this.player.getMainHandStack().getItem() instanceof Gun) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "handleBlockBreaking", at = @At("HEAD"), cancellable = true)
    public void handleBlockBreaking(boolean breaking, CallbackInfo ci) {
        if (this.player.getMainHandStack().getItem() instanceof Gun) {
            ci.cancel();
        }
    }

    @Inject(method = "doItemUse", at = @At("HEAD"), cancellable = true)
    public void doItemUse(CallbackInfo ci) {
        if (this.player.getMainHandStack().getItem() instanceof Gun) {
            ci.cancel();
        }
    }

}
