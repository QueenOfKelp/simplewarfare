package queenofkelp.simplewarfare.util.gun;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import queenofkelp.simplewarfare.util.IEntityDataSaver;

public class GunShooterUtil {

    public static int getPlayerReloadTime(PlayerEntity player) {
        NbtCompound playerNbt = ((IEntityDataSaver) player).getPersistentData();

        return (playerNbt.get("ReloadTime") != null) ? playerNbt.getInt("ReloadTime") : -1;
    }
    public static void setPlayerReloadTime(PlayerEntity player, int time) {
        NbtCompound playerNbt = ((IEntityDataSaver) player).getPersistentData();

        playerNbt.putInt("ReloadTime", time);
    }
    public static int getPlayerGunPullOutTime(PlayerEntity player) {
        NbtCompound playerNbt = ((IEntityDataSaver) player).getPersistentData();

        return (playerNbt.get("GunPullOutTime") != null) ? playerNbt.getInt("GunPullOutTime") : 0;
    }
    public static void setPlayerGunPullOutTime(PlayerEntity player, int time) {
        NbtCompound playerNbt = ((IEntityDataSaver) player).getPersistentData();

        playerNbt.putInt("GunPullOutTime", time);
    }

}
