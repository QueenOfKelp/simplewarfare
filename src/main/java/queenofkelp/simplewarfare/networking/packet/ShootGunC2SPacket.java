package queenofkelp.simplewarfare.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import queenofkelp.simplewarfare.gun.Gun;
import queenofkelp.simplewarfare.util.IEntityDataSaver;

public class ShootGunC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {

        ItemStack item = player.getInventory().getMainHandStack();
        NbtCompound nbt = ((IEntityDataSaver) player).getPersistentData();
        //!nbt.getBoolean("reloading") &&

        boolean gunWasShot = false;

        if (item.getItem() instanceof Gun gun && !player.getItemCooldownManager().isCoolingDown(gun)) {
            if (gunWasShot && !gun.getIsAutomatic()) {
                return;
            }

            gun.onFired(player.getWorld(), player, item);
        }
    }
}
