package queenofkelp.simplewarfare.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import queenofkelp.simplewarfare.util.gun.GunShooterUtil;

import java.util.Objects;
import java.util.UUID;

public class SyncPlayerReloadingS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender packetSender) {

        UUID playerUUID = buf.readUuid();
        int reloadTime = buf.readInt();

        if (client.player == null || client.player.getWorld() == null || client.player.getWorld().getPlayerByUuid(playerUUID) == null) {
            return;
        }

        GunShooterUtil.setPlayerReloadTime(client.player.getWorld().getPlayerByUuid(playerUUID), reloadTime);

    }
}
