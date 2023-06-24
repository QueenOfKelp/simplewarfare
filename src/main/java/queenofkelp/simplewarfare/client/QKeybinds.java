package queenofkelp.simplewarfare.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import org.lwjgl.glfw.GLFW;
import queenofkelp.simplewarfare.networking.QPackets;

public class QKeybinds {

    public static KeyBinding reloadKey;
    public static KeyBinding shootKey;

    public static void initialize() {

        reloadKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.lastshot.reload", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_R, // The keycode of the key
                "category.lastshot" // The translation key of the keybinding's category.
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (reloadKey.wasPressed()) {
                //ClientPlayNetworking.send(ModPackets.C2S_RELOAD_GUN_ID, PacketByteBufs.create());
            }
        });

        shootKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.lastshot.shoot", // The translation key of the keybinding's name
                InputUtil.Type.MOUSE, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_MOUSE_BUTTON_1, // The keycode of the key
                "category.lastshot" // The translation key of the keybinding's category.
        ));

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (shootKey.isPressed()) {
                ClientPlayNetworking.send(QPackets.C2S_SHOOT, PacketByteBufs.create());
            }
        });

    }

}
