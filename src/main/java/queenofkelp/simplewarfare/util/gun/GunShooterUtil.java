package queenofkelp.simplewarfare.util.gun;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import queenofkelp.simplewarfare.gun.item.Gun;

import java.util.HashMap;

public class GunShooterUtil {

    public static GunShooterUtil INSTANCE = new GunShooterUtil();

    public HashMap<PlayerEntity, Integer> playerReloadTimes = new HashMap<>();
    public HashMap<PlayerEntity, Integer> playerGunPulloutTimes = new HashMap<>();
    public HashMap<PlayerEntity, Vec3d> playerPreviousPositions = new HashMap<>();
    public HashMap<PlayerEntity, Boolean> playerADS = new HashMap<>();
    public HashMap<PlayerEntity, Long> lastPlayerShootTimes = new HashMap<>();

    public static int getPlayerReloadTime(PlayerEntity player) {
        return (INSTANCE.playerReloadTimes.get(player) != null) ? INSTANCE.playerReloadTimes.get(player) : -1;
    }
    public static void setPlayerReloadTime(PlayerEntity player, int time) {
        INSTANCE.playerReloadTimes.put(player, time);
    }
    public static boolean isPlayerReloading(PlayerEntity player) {
        return INSTANCE.playerReloadTimes.get(player) != null && INSTANCE.playerReloadTimes.get(player) > -1;
    }
    public static int getPlayerGunPullOutTime(PlayerEntity player) {
        return (INSTANCE.playerGunPulloutTimes.get(player) != null) ? INSTANCE.playerGunPulloutTimes.get(player) : 0;
    }
    public static void setPlayerGunPullOutTime(PlayerEntity player, int time) {
        INSTANCE.playerGunPulloutTimes.put(player, time);
    }
    public static boolean isPlayerPullingOutGun(PlayerEntity player) {
        return INSTANCE.playerGunPulloutTimes.get(player) != null && INSTANCE.playerGunPulloutTimes.get(player) > 0;
    }
    public static Vec3d getPlayerPreviousPosition(PlayerEntity player) {
        return (INSTANCE.playerPreviousPositions.get(player) != null) ? INSTANCE.playerPreviousPositions.get(player) : player.getPos();
    }
    public static void setPlayerPreviousPosition(PlayerEntity player, Vec3d position) {
        INSTANCE.playerPreviousPositions.put(player, position);
    }
    public static boolean isPlayerADSing(PlayerEntity player) {
        ItemStack gunStack = player.getMainHandStack();
        if (gunStack.getItem() instanceof Gun gun) {
            return INSTANCE.playerADS.get(player) != null && INSTANCE.playerADS.get(player) && gun.getBloom(gunStack).canADS;
        }
        return false;
    }
    public static void setPlayerADS(PlayerEntity player, boolean isADSing) {
        INSTANCE.playerADS.put(player, isADSing);
    }
    public static long getPlayerLastShootTime(PlayerEntity player) {
        return (INSTANCE.lastPlayerShootTimes.get(player) != null) ? INSTANCE.lastPlayerShootTimes.get(player) : player.getWorld().getTime();
    }
    public static void setPlayerLastShootTime(PlayerEntity player, long time) {
        INSTANCE.lastPlayerShootTimes.put(player, time);
    }
    public static boolean hasPlayerShotInLastTicks(PlayerEntity player, long ticks) {
        return INSTANCE.lastPlayerShootTimes.get(player) != null && player.getWorld().getTime() - ticks <= INSTANCE.lastPlayerShootTimes.get(player);
    }
}
