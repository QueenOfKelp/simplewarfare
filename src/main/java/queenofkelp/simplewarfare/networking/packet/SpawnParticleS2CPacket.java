package queenofkelp.simplewarfare.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;

public class SpawnParticleS2CPacket {

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender packetSender) {

        long[] data = buf.readLongArray();

        double x = Double.longBitsToDouble(data[0]);
        double y = Double.longBitsToDouble(data[1]);
        double z = Double.longBitsToDouble(data[2]);

        double velocityX = Double.longBitsToDouble(data[3]);
        double velocityY = Double.longBitsToDouble(data[4]);
        double velocityZ = Double.longBitsToDouble(data[5]);

        String s = buf.readString();
        ParticleEffect particleType;

        if (s.equals(ParticleTypes.CAMPFIRE_COSY_SMOKE.asString())) {
            particleType = ParticleTypes.CAMPFIRE_COSY_SMOKE;
        }
        else {
            System.out.print("particle was not found in possible particles");
            particleType = ParticleTypes.ANGRY_VILLAGER;
        }

        if (client.player != null) {
            client.player.getWorld().addParticle(particleType, true, x, y, z, velocityX, velocityY, velocityZ);
        }
    }

}
