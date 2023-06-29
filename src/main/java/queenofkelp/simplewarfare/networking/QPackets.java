package queenofkelp.simplewarfare.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import queenofkelp.simplewarfare.SimpleWarfare;
import queenofkelp.simplewarfare.networking.packet.DoRecoilS2CPacket;
import queenofkelp.simplewarfare.networking.packet.ShootGunC2SPacket;
import queenofkelp.simplewarfare.networking.packet.SpawnTracerS2CPacket;

public class QPackets {


    public static final Identifier S2C_DO_RECOIL = SimpleWarfare.getIdentifier("recoil");
    public static final Identifier C2S_SHOOT = SimpleWarfare.getIdentifier("shoot");
    public static final Identifier S2C_SPAWN_TRACER = SimpleWarfare.getIdentifier("tracer_render");



    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(C2S_SHOOT, ShootGunC2SPacket::receive);
    }

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(S2C_DO_RECOIL, DoRecoilS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(S2C_SPAWN_TRACER, SpawnTracerS2CPacket::recieve);
    }



}
