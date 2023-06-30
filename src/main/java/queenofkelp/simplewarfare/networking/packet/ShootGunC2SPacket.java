package queenofkelp.simplewarfare.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import queenofkelp.simplewarfare.gun.item.Gun;
import queenofkelp.simplewarfare.networking.QPackets;
import queenofkelp.simplewarfare.util.gun.GunShooterUtil;

import java.util.Objects;

public class ShootGunC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {

        boolean gunWasShotBefore = buf.readBoolean();

        ItemStack gunStack = player.getInventory().getMainHandStack();

        if (gunStack.getItem() instanceof Gun gun && !player.getItemCooldownManager().isCoolingDown(gun)) {
            if (gunWasShotBefore && !gun.getIsAutomatic(gunStack)) {
                return;
            }

            gun.onFired(player.getWorld(), player, gunStack);
        }

        GunShooterUtil.setPlayerLastShootTime(player, player.getWorld().getTime());
        for (ServerPlayerEntity sp : Objects.requireNonNull(player.getServer()).getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(sp, QPackets.S2C_SYNC_SHOOT_TIME, QPackets.makeSyncPlayerShootTimeBuffer(player, player.getWorld().getTime()));
        }
    }
}
