package queenofkelp.simplewarfare.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.Identifier;
import queenofkelp.simplewarfare.SimpleWarfare;
import queenofkelp.simplewarfare.networking.packet.*;

public class QPackets {

    public static final Identifier S2C_DO_RECOIL = SimpleWarfare.getIdentifier("recoil");
    public static final Identifier C2S_SHOOT = SimpleWarfare.getIdentifier("shoot");
    public static final Identifier S2C_SPAWN_PARTICLE = SimpleWarfare.getIdentifier("spawn_particle");
    public static final Identifier C2S_RELOAD = SimpleWarfare.getIdentifier("reload");
    public static final Identifier S2C_SYNC_RELOAD = SimpleWarfare.getIdentifier("sync_reload");
    public static final Identifier S2C_SYNC_PULLOUT = SimpleWarfare.getIdentifier("sync_pullout");
    public static final Identifier C2S_SYNC_ADS = SimpleWarfare.getIdentifier("sync_ads");
    public static final Identifier S2C_SYNC_ADS = SimpleWarfare.getIdentifier("sync_ads_to_clients");


    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(C2S_SHOOT, ShootGunC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(C2S_RELOAD, ReloadGunC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(C2S_SYNC_ADS, SyncPlayerADSingC2SPacket::receive);
    }

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(S2C_DO_RECOIL, DoRecoilS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(S2C_SPAWN_PARTICLE, SpawnParticleS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(S2C_SYNC_RELOAD, SyncPlayerReloadingS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(S2C_SYNC_PULLOUT, SyncPlayerPulloutS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(S2C_SYNC_ADS, SyncPlayerADSingS2CPacket::receive);
    }

    public static PacketByteBuf makeSpawnParticlesBuffer(ParticleEffect effect, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        PacketByteBuf buffer = PacketByteBufs.create();

        long[] data = new long[6];
        data[0] = Double.doubleToLongBits(x);
        data[1] = Double.doubleToLongBits(y);
        data[2] = Double.doubleToLongBits(z);
        data[3] = Double.doubleToLongBits(velocityX);
        data[4] = Double.doubleToLongBits(velocityY);
        data[5] = Double.doubleToLongBits(velocityZ);

        buffer.writeLongArray(data);

        buffer.writeString(effect.asString());

        return buffer;
    }

    public static PacketByteBuf makeSyncPlayerReloadingBuffer(PlayerEntity player, int reloadTime) {
        PacketByteBuf buffer = PacketByteBufs.create();

        buffer.writeString(player.getGameProfile().getName());
        buffer.writeInt(reloadTime);

        return buffer;
    }

    public static PacketByteBuf makeSyncPlayerPulloutBuffer(PlayerEntity player, int pulloutTime) {
        PacketByteBuf buffer = PacketByteBufs.create();

        buffer.writeString(player.getGameProfile().getName());
        buffer.writeInt(pulloutTime);

        return buffer;
    }

    public static PacketByteBuf makeSyncPlayerADSBuffer(PlayerEntity player, boolean ADS) {
        PacketByteBuf buffer = PacketByteBufs.create();

        buffer.writeString(player.getGameProfile().getName());
        buffer.writeBoolean(ADS);

        return buffer;
    }
}
