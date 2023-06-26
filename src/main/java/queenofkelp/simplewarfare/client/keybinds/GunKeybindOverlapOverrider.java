package queenofkelp.simplewarfare.client.keybinds;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import queenofkelp.simplewarfare.gun.item.Gun;
import queenofkelp.simplewarfare.mixin.KeyBindingMixin;

public class GunKeybindOverlapOverrider {

    public static GunKeybindOverlapOverrider INSTANCE = new GunKeybindOverlapOverrider();

    public Multimap<InputUtil.Key, KeyBinding> keybindsMap = ArrayListMultimap.create();

    public void putKey(InputUtil.Key key, KeyBinding keyBinding) {
        keybindsMap.put(key, keyBinding);
    }

    public void onKeyPressed(InputUtil.Key key) {
        KeyBinding currentKeybind = null;
        for (KeyBinding keybind : keybindsMap.get(key)) {
            if (checkIfKeybindIsShootOrADS(keybind)) {
                if (checkIfClientPlayerHoldingGun()) {
                    ((KeyBindingMixin.TimesPressedAccessor) keybind).setTimesPressed(((KeyBindingMixin.TimesPressedAccessor) keybind).getTimesPressed() + 1);
                    return;
                }
            }
            else {
                currentKeybind = keybind;
            }
        }
        if (currentKeybind == null) {
            return;
        }
        ((KeyBindingMixin.TimesPressedAccessor) currentKeybind).setTimesPressed(((KeyBindingMixin.TimesPressedAccessor) currentKeybind).getTimesPressed() + 1);
    }

    public void setKeyPressed(InputUtil.Key key, boolean pressed) {
        KeyBinding currentKeybind = null;
        for (KeyBinding keybind : keybindsMap.get(key)) {
            if (checkIfKeybindIsShootOrADS(keybind)) {
                if (checkIfClientPlayerHoldingGun()) {
                    keybind.setPressed(pressed);
                    return;
                }
            }
            else {
                currentKeybind = keybind;
            }
        }
        if (currentKeybind == null) {
            return;
        }
        currentKeybind.setPressed(pressed);
    }

    public boolean checkIfKeybindIsShootOrADS(KeyBinding keybind) {
        return keybind.getTranslationKey().equals("key.simplewarfare.shoot") || keybind.getTranslationKey().equals("key.simplewarfare.ads");
    }

    public boolean checkIfClientPlayerHoldingGun() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return false;
        }

        return ((KeyBindingMixin.SelectedItemAccessor) player).getSelectedItem().getItem() instanceof Gun;
    }
}
