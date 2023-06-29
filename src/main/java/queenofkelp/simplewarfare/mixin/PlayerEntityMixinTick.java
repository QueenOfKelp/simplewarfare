package queenofkelp.simplewarfare.mixin;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import queenofkelp.simplewarfare.gun.item.Gun;
import queenofkelp.simplewarfare.networking.QPackets;
import queenofkelp.simplewarfare.util.gun.GunShooterUtil;

import java.util.Objects;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixinTick extends LivingEntity {

    @Shadow public abstract PlayerInventory getInventory();

    protected PlayerEntityMixinTick(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    ItemStack lastItemStack;
    ItemStack itemStack = new ItemStack(Items.AIR);
    Vec3d currentPos = this.getPos();
    Vec3d lastPos = this.getPos();

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {

        PlayerEntity player = (PlayerEntity) (LivingEntity) this;
        lastItemStack = itemStack.copy();
        itemStack = this.getInventory().getMainHandStack();
        lastPos = new Vec3d(currentPos.getX(), currentPos.getY(), currentPos.getZ());
        currentPos = new Vec3d(player.getX(), player.getY(), player.getZ());

        GunShooterUtil.setPlayerPreviousPosition(player, lastPos);

        Gun gun = (itemStack.getItem() instanceof Gun) ? (Gun) itemStack.getItem() : null;
        if (gun == null) {
            return;
        }

        if (this.getWorld().isClient) {
            return;
        }

        boolean itemStackAndLastItemStackAreDifferent = !lastItemStack.getOrCreateNbt().equals(itemStack.getOrCreateNbt());
        int reloadTime = GunShooterUtil.getPlayerReloadTime(player);

        if (itemStackAndLastItemStackAreDifferent) {
            player.getItemCooldownManager().set(gun, gun.getEquipTime(itemStack));
            GunShooterUtil.setPlayerGunPullOutTime(player, gun.getEquipTime(itemStack));
            for (ServerPlayerEntity sp : Objects.requireNonNull(player.getServer()).getPlayerManager().getPlayerList()) {
                ServerPlayNetworking.send(sp, QPackets.S2C_SYNC_PULLOUT, QPackets.makeSyncPlayerPulloutBuffer(player, gun.getEquipTime(itemStack)));
            }
            GunShooterUtil.setPlayerReloadTime(player, -1);
        }
        else if (reloadTime >= 0) {
            if (reloadTime == 0) {
                gun.reload(player, itemStack);
                for (ServerPlayerEntity sp : Objects.requireNonNull(player.getServer()).getPlayerManager().getPlayerList()) {
                    ServerPlayNetworking.send(sp, QPackets.S2C_SYNC_RELOAD, QPackets.makeSyncPlayerReloadingBuffer(player, -1));
                }
            }
            GunShooterUtil.setPlayerReloadTime(player, reloadTime - 1);
        }



        int gunPulloutTime = GunShooterUtil.getPlayerGunPullOutTime(player);
        if (gunPulloutTime > 0) {
            gunPulloutTime--;
            GunShooterUtil.setPlayerGunPullOutTime(player, gunPulloutTime);
            if (gunPulloutTime == 0) {
                for (ServerPlayerEntity sp : Objects.requireNonNull(player.getServer()).getPlayerManager().getPlayerList()) {
                    ServerPlayNetworking.send(sp, QPackets.S2C_SYNC_PULLOUT, QPackets.makeSyncPlayerPulloutBuffer(player, 0));
                }
            }
        }

    }

}
