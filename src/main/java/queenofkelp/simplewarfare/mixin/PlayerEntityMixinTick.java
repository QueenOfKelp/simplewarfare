package queenofkelp.simplewarfare.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import queenofkelp.simplewarfare.gun.item.Gun;
import queenofkelp.simplewarfare.util.gun.GunShooterUtil;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixinTick extends LivingEntity {

    @Shadow public abstract PlayerInventory getInventory();

    protected PlayerEntityMixinTick(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    ItemStack lastItemStack;
    ItemStack itemStack = new ItemStack(Items.AIR);

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {

        PlayerEntity player = (PlayerEntity) (LivingEntity) this;
        lastItemStack = itemStack.copy();
        itemStack = this.getInventory().getMainHandStack();

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
            player.getItemCooldownManager().set(gun, gun.getEquipTime());
            GunShooterUtil.setPlayerGunPullOutTime(player, gun.getEquipTime());
            GunShooterUtil.setPlayerReloadTime(player, -1);
        }
        else if (reloadTime >= 0) {
            if (reloadTime == 0) {
                gun.reload(player, itemStack);
            }
            GunShooterUtil.setPlayerReloadTime(player, reloadTime - 1);
        }



        int gunPulloutTime = GunShooterUtil.getPlayerGunPullOutTime(player);
        if (gunPulloutTime > 0) {
            GunShooterUtil.setPlayerGunPullOutTime(player, gunPulloutTime - 1);
        }
    }

}
