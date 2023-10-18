package queenofkelp.simplewarfare.client.keybinds;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import org.lwjgl.glfw.GLFW;
import queenofkelp.simplewarfare.networking.QPackets;

public class QKeybinds {

    public static KeyBinding reloadKey;

    public boolean hasPressedShootKey = false;
    public boolean wasADSed;

    public QKeybinds() {

    }

    public static QKeybinds INSTANCE = new QKeybinds();

    public static void initialize() {

        reloadKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.simplewarfare.reload",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.simplewarfare"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (reloadKey.wasPressed()) {
                ClientPlayNetworking.send(QPackets.C2S_RELOAD, PacketByteBufs.create());
            }
        });

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (MinecraftClient.getInstance().options.attackKey.isPressed()) {
                PacketByteBuf packet = PacketByteBufs.create();
                packet.writeBoolean(INSTANCE.hasPressedShootKey);
                ClientPlayNetworking.send(QPackets.C2S_SHOOT, packet);
                INSTANCE.hasPressedShootKey = true;
            }
            else {
                INSTANCE.hasPressedShootKey = false;
            }
        });

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                if (MinecraftClient.getInstance().options.useKey.isPressed() != INSTANCE.wasADSed) {
                    ClientPlayNetworking.send(QPackets.C2S_SYNC_ADS, QPackets.makeSyncPlayerADSBuffer(client.player, MinecraftClient.getInstance().options.useKey.isPressed()));
                }
                INSTANCE.wasADSed = MinecraftClient.getInstance().options.useKey.isPressed();
            }
        });
    }

}
