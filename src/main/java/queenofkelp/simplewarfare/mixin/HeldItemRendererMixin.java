package queenofkelp.simplewarfare.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import queenofkelp.simplewarfare.gun.item.Gun;
import queenofkelp.simplewarfare.util.gun.GunShooterUtil;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {
    @Shadow protected abstract void applyEquipOffset(MatrixStack matrices, Arm arm, float equipProgress);
    @Shadow public abstract void renderItem(LivingEntity entity, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light);

    @Shadow protected abstract void applySwingOffset(MatrixStack matrices, Arm arm, float swingProgress);

    @Inject(method = "renderFirstPersonItem", at = @At("HEAD"), cancellable = true)
    private void renderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (item.getItem() instanceof Gun gunItem) {
            if (!gunItem.gunHasDefaultAnimations()) {
                return;
            }
            boolean reloadingOrPullingOut = GunShooterUtil.isPlayerPullingOutGun(player) || GunShooterUtil.isPlayerReloading(player);
            Arm arm = (hand == Hand.MAIN_HAND) ? player.getMainArm() : player.getMainArm().getOpposite();
            boolean mainArmIsRight = arm == Arm.RIGHT;
            int offsetForMainArm = mainArmIsRight ? 1 : -1;
            float x;
            float y = 1f;
            float z;
            if (reloadingOrPullingOut) {
                this.applyEquipOffset(matrices, arm, equipProgress);
                matrices.translate(((float)offsetForMainArm * -0.4785682F), -0.0943870022892952, 0.05731530860066414);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-2.935F));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)offsetForMainArm * 65.3F));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)offsetForMainArm * -2.785F));

                matrices.translate((y * 0.0F), (y * 0.0F), (double)(y * 0.04F));
                matrices.scale(1.0F, 1.0F, 1.0F + y * 0.2F);
                matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees((float)offsetForMainArm * 45.0F));
            } else {
                x = -0.4F * MathHelper.sin(MathHelper.sqrt(swingProgress) * 3.1415927F);
                y = 0.2F * MathHelper.sin(MathHelper.sqrt(swingProgress) * 6.2831855F);
                z = -0.2F * MathHelper.sin(swingProgress * 3.1415927F);
                matrices.translate((double)((float)offsetForMainArm * x), (double)y, (double)z);
                this.applySwingOffset(matrices, arm, swingProgress);
                if (GunShooterUtil.isPlayerADSing(player) && hand == Hand.MAIN_HAND) {
                    matrices.translate(((float)offsetForMainArm * -0.541864F), .13, 0.0);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)offsetForMainArm));
                    this.applyEquipOffset(matrices, arm, -equipProgress/12.5f);
                }
                else {
                    this.applyEquipOffset(matrices, arm, -equipProgress/4.5f);
                }
            }

            this.renderItem(player, item, mainArmIsRight ? ModelTransformationMode.FIRST_PERSON_RIGHT_HAND : ModelTransformationMode.FIRST_PERSON_LEFT_HAND, !mainArmIsRight, matrices, vertexConsumers, light);
            ci.cancel();
        }
    }
}
