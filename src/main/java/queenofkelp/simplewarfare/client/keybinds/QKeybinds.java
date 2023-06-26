package queenofkelp.simplewarfare.client.keybinds;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import queenofkelp.simplewarfare.networking.QPackets;

public class QKeybinds {

    public static KeyBinding reloadKey;
    public static KeyBinding shootKey;
    public static KeyBinding ADSKey;

    public static void initialize() {

        reloadKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.simplewarfare.reload",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.simplewarfare"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (reloadKey.wasPressed()) {
                //ClientPlayNetworking.send(QPackets.C2S_RELOAD_GUN, PacketByteBufs.create());
            }
        });

        shootKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.simplewarfare.shoot",
                InputUtil.Type.MOUSE,
                GLFW.GLFW_MOUSE_BUTTON_1,
                "category.simplewarfare"
        ));

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (shootKey.isPressed()) {
                ClientPlayNetworking.send(QPackets.C2S_SHOOT, PacketByteBufs.create());
            }
        });

        ADSKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.simplewarfare.ads",
                InputUtil.Type.MOUSE,
                GLFW.GLFW_MOUSE_BUTTON_2,
                "category.simplewarfare"
        ));
    }

}
