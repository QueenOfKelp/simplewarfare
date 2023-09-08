package queenofkelp.simplewarfare.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import queenofkelp.simplewarfare.networking.QPackets;
import queenofkelp.simplewarfare.util.gun.GunShooterUtil;

import java.util.Objects;
import java.util.UUID;

public class SyncPlayerADSingC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {

        UUID playerUUID = buf.readUuid();
        boolean playerADSing = buf.readBoolean();

        GunShooterUtil.setPlayerADS(server.getPlayerManager().getPlayer(playerUUID), playerADSing);

        for (ServerPlayerEntity sp : Objects.requireNonNull(player.getServer()).getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(sp, QPackets.S2C_SYNC_ADS, QPackets.makeSyncPlayerADSBuffer(player, playerADSing));
        }
    }
}
