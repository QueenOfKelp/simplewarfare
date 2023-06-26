package queenofkelp.simplewarfare.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import queenofkelp.simplewarfare.client.keybinds.GunKeybindOverlapOverrider;

import java.util.Map;

@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin {

    @Final
    @Shadow
    private static Map<String, KeyBinding> KEYS_BY_ID;
    @Shadow private InputUtil.Key boundKey;

    @Inject(method = "onKeyPressed", at = @At(value = "HEAD"), cancellable = true)
    private static void onKeyPressedFixed(InputUtil.Key key, CallbackInfo ci){
        GunKeybindOverlapOverrider.INSTANCE.onKeyPressed(key);
        ci.cancel();
    }

    @Inject(method = "setKeyPressed", at = @At(value = "HEAD"), cancellable = true)
    private static void setKeyPressedFixed(InputUtil.Key key, boolean pressed, CallbackInfo ci){
        GunKeybindOverlapOverrider.INSTANCE.setKeyPressed(key, pressed);
        ci.cancel();
    }

    @Inject(method = "updateKeysByCode", at = @At(value = "TAIL"))
    private static void updateByCodeToMultiMap(CallbackInfo ci) {
        GunKeybindOverlapOverrider.INSTANCE.keybindsMap.clear();
        for (KeyBinding keyBinding : KEYS_BY_ID.values()) {
            GunKeybindOverlapOverrider.INSTANCE.putKey(((BoundKeyAccessor) keyBinding).getBoundKey(),keyBinding);
        }
    }

    @Inject(method = "<init>(Ljava/lang/String;Lnet/minecraft/client/util/InputUtil$Type;ILjava/lang/String;)V", at = @At(value = "TAIL"))
    private void putToMultiMap(String translationKey, InputUtil.Type type, int code, String category, CallbackInfo ci){
        GunKeybindOverlapOverrider.INSTANCE.putKey(boundKey, (KeyBinding) (Object) this);
    }

    @Mixin(KeyBinding.class)
    public interface TimesPressedAccessor {
        @Accessor(value = "timesPressed")
        int getTimesPressed();
        @Accessor(value = "timesPressed")
        void setTimesPressed(int value);
    }

    @Mixin(KeyBinding.class)
    public interface BoundKeyAccessor {
        @Accessor(value = "boundKey")
        InputUtil.Key getBoundKey();
    }

    @Mixin(PlayerEntity.class)
    public interface SelectedItemAccessor {
        @Accessor(value = "selectedItem")
        ItemStack getSelectedItem();
    }
}
