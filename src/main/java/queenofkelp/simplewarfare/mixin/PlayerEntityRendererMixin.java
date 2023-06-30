package queenofkelp.simplewarfare.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import queenofkelp.simplewarfare.gun.item.Gun;
import queenofkelp.simplewarfare.util.gun.GunShooterUtil;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {

    @Inject(method = "getArmPose", at = @At("HEAD"), cancellable = true)
    private static void getArmPose(AbstractClientPlayerEntity player, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {

        ItemStack itemStack = player.getStackInHand(hand);
        Item gunItem = itemStack.getItem();

        if (!(gunItem instanceof Gun)) {
            return;
        }
        if (!((Gun) gunItem).gunHasDefaultAnimations()) {
            return;
        }

        if (player.getActiveHand() == hand) {
            if (GunShooterUtil.isPlayerReloading(player) || GunShooterUtil.isPlayerPullingOutGun(player)) {
                cir.setReturnValue(BipedEntityModel.ArmPose.CROSSBOW_CHARGE);
            }
            else if (GunShooterUtil.isPlayerADSing(player) || GunShooterUtil.hasPlayerShotInLastTicks(player, 3)) {
                cir.setReturnValue(BipedEntityModel.ArmPose.CROSSBOW_HOLD);
            }
            else {
                cir.setReturnValue(BipedEntityModel.ArmPose.CROSSBOW_CHARGE);
            }
        }

    }
}
