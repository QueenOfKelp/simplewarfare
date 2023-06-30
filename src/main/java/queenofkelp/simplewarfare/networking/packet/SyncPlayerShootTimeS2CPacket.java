package queenofkelp.simplewarfare.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import queenofkelp.simplewarfare.util.gun.GunShooterUtil;

import java.util.Objects;

public class SyncPlayerShootTimeS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender packetSender) {

        String playerName = buf.readString();
        long shootTime = buf.readLong();

        GunShooterUtil.setPlayerLastShootTime(Objects.requireNonNull(client.getServer()).getPlayerManager().getPlayer(playerName), shootTime);

    }
}
