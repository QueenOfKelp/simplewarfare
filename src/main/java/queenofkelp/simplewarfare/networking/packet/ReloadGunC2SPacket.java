package queenofkelp.simplewarfare.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import queenofkelp.simplewarfare.bullet.item.BulletItem;
import queenofkelp.simplewarfare.gun.item.Gun;
import queenofkelp.simplewarfare.util.gun.GunShooterUtil;

public class ReloadGunC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {

        ItemStack gunStack = player.getInventory().getMainHandStack();

        if (gunStack.getItem() instanceof Gun gun && GunShooterUtil.getPlayerReloadTime(player) < 0 && !player.getItemCooldownManager().isCoolingDown(gun)) {
            if (gun.getAmmo(gunStack) >= gun.getMaxAmmo()) {
                return;
            }

            gun.checkResetBulletLoaded(gunStack);

            boolean itemFound = false;

            for (int i = 0; i < player.getInventory().size(); i++) {
                ItemStack itemFromSlot = player.getInventory().getStack(i);
                if (itemFromSlot.getItem() instanceof BulletItem bulletItem && bulletItem.type == gun.getAmmoType()) {
                    if (itemFromSlot.getItem() == gun.getBulletItemLoaded(gunStack) || gun.getBulletItemLoaded(gunStack) == null) {
                        itemFound = true;
                        break;
                    }
                }
            }

            if (!itemFound) {
                return;
            }

            player.getItemCooldownManager().set(gun, gun.getReloadTime());
            GunShooterUtil.setPlayerReloadTime(player, gun.getReloadTime());
            gun.playReloadStartSound(player, gunStack);
        }
    }
}
