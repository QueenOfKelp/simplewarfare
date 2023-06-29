package queenofkelp.simplewarfare.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import queenofkelp.simplewarfare.bullet.item.BulletItem;
import queenofkelp.simplewarfare.gun.item.Gun;
import queenofkelp.simplewarfare.networking.QPackets;
import queenofkelp.simplewarfare.util.gun.GunShooterUtil;

import java.util.Objects;

public class ReloadGunC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {

        ItemStack gunStack = player.getInventory().getMainHandStack();

        if (gunStack.getItem() instanceof Gun gun && GunShooterUtil.getPlayerReloadTime(player) < 0 && !player.getItemCooldownManager().isCoolingDown(gun)) {
            if (gun.getAmmo(gunStack) >= gun.getMaxAmmo(gunStack)) {
                return;
            }

            gun.checkResetBulletLoaded(gunStack);

            boolean itemFound = false;

            for (int i = 0; i < player.getInventory().size(); i++) {
                ItemStack itemFromSlot = player.getInventory().getStack(i);
                if (itemFromSlot.getItem() instanceof BulletItem bulletItem && bulletItem.type == gun.getAmmoType(gunStack)) {
                    if (itemFromSlot.getItem() == gun.getBulletItemLoaded(gunStack) || gun.getBulletItemLoaded(gunStack) == null) {
                        itemFound = true;
                        break;
                    }
                }
            }

            if (!itemFound) {
                return;
            }

            player.getItemCooldownManager().set(gun, gun.getReloadTime(gunStack));
            GunShooterUtil.setPlayerReloadTime(player, gun.getReloadTime(gunStack));
            for (ServerPlayerEntity sp : Objects.requireNonNull(player.getServer()).getPlayerManager().getPlayerList()) {
                ServerPlayNetworking.send(sp, QPackets.S2C_SYNC_RELOAD, QPackets.makeSyncPlayerReloadingBuffer(player, gun.getReloadTime(gunStack)));
            }
            gun.playReloadStartSound(player, gunStack);
        }
    }
}
