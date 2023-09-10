package queenofkelp.simplewarfare.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.client.util.SmoothUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MouseMixin {

    @Shadow @Final private MinecraftClient client;

    @Shadow private double cursorDeltaY;

    @Shadow private double cursorDeltaX;

    @Shadow @Final private SmoothUtil cursorXSmoother;

    @Shadow @Final private SmoothUtil cursorYSmoother;

    @Shadow private double lastMouseUpdateTime;

    @Shadow public abstract boolean isCursorLocked();

    @Inject(method = "updateMouse", at = @At("HEAD"))
    public void adjustSensitivityForZoom(CallbackInfo ci) {
        double d = GlfwUtil.getTime();
        double e = d - this.lastMouseUpdateTime;
        this.lastMouseUpdateTime = d;
        if (this.isCursorLocked() && this.client.isWindowFocused()) {
            double f = (Double)this.client.options.getMouseSensitivity().getValue() * 0.6000000238418579 + 0.20000000298023224;
            double g = (client.player == null) ? 0 : f * f * f * (this.client.player.getFovMultiplier() * 8);

            double k;
            double l;
            if (this.client.options.smoothCameraEnabled) {
                double i = this.cursorXSmoother.smooth(this.cursorDeltaX * g, e * g);
                double j = this.cursorYSmoother.smooth(this.cursorDeltaY * g, e * g);
                k = i;
                l = j;
            } else {
                this.cursorXSmoother.clear();
                this.cursorYSmoother.clear();
                k = this.cursorDeltaX * g;
                l = this.cursorDeltaY * g;
            }

            this.cursorDeltaX = 0.0;
            this.cursorDeltaY = 0.0;
            int m = 1;
            if ((Boolean)this.client.options.getInvertYMouse().getValue()) {
                m = -1;
            }

            this.client.getTutorialManager().onUpdateMouse(k, l);
            if (this.client.player != null) {
                this.client.player.changeLookDirection(k, l * (double)m);
            }

        } else {
            this.cursorDeltaX = 0.0;
            this.cursorDeltaY = 0.0;
        }
    }

}
