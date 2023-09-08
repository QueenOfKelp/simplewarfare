package queenofkelp.simplewarfare.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import queenofkelp.simplewarfare.util.gun.GunShooterUtil;

import java.util.Objects;
import java.util.UUID;

public class SyncPlayerADSingS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender packetSender) {

        UUID playerUUID = buf.readUuid();
        boolean ads = buf.readBoolean();

        if (client.player == null || client.player.getWorld() == null || client.player.getWorld().getPlayerByUuid(playerUUID) == null) {
            return;
        }

        GunShooterUtil.setPlayerADS(client.player.getWorld().getPlayerByUuid(playerUUID), ads);

    }
}
