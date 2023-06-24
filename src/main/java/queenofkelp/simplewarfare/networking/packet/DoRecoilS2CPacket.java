package queenofkelp.simplewarfare.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class DoRecoilS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                              PacketByteBuf buf, PacketSender packetSender) {

        float recoil = buf.readFloat();

        if (client.player != null) {
            client.player.setPitch(client.player.getPitch() - recoil);
        }

    }
}
