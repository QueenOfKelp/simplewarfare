package queenofkelp.simplewarfare.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import queenofkelp.simplewarfare.bullet.entity.BulletEntity;
import queenofkelp.simplewarfare.bullet.item.AmmoType;
import queenofkelp.simplewarfare.bullet.item.BulletItem;
import queenofkelp.simplewarfare.gun.GunSound;
import queenofkelp.simplewarfare.gun.Gun;
import queenofkelp.simplewarfare.util.damage_dropoff.DamageDropoff;

import java.util.ArrayList;

public class SpawnTracerS2CPacket {

    public static void recieve(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender packetSender) {
        client.execute(() -> {
            Gun.world.spawnEntity(Gun.bulletEntity);
        });
    }
}
