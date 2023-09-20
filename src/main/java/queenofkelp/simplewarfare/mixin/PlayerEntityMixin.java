package queenofkelp.simplewarfare.mixin;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import queenofkelp.simplewarfare.SimpleWarfare;
import queenofkelp.simplewarfare.bulletproof_armor.BulletproofArmorMaterial;
import queenofkelp.simplewarfare.gun.item.Gun;
import queenofkelp.simplewarfare.networking.QPackets;
import queenofkelp.simplewarfare.registry.QTags;
import queenofkelp.simplewarfare.util.gun.GunShooterUtil;

import java.util.Objects;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    @Shadow public abstract PlayerInventory getInventory();

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    ItemStack lastItemStack;
    ItemStack itemStack = new ItemStack(Items.AIR);
    Vec3d currentPos = this.getPos();
    Vec3d lastPos = this.getPos();
    EntityAttributeModifier speedModifier = new EntityAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED.getTranslationKey(), 1, EntityAttributeModifier.Operation.MULTIPLY_BASE);
    float previousSpeed;

    public void addGunSpeedEffect(PlayerEntity player, float speed) {
        removeGunSpeedEffect(player);
        speedModifier = new EntityAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED.getTranslationKey(), speed, EntityAttributeModifier.Operation.MULTIPLY_BASE);
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).addTemporaryModifier(speedModifier);
    }

    public void removeGunSpeedEffect(PlayerEntity player) {
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).removeModifier(speedModifier);
    }

    float speed;

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
            removeGunSpeedEffect(player);
            return;
        }

        if (this.getWorld().isClient) {
            return;
        }

        speed = GunShooterUtil.isPlayerADSing(player) ? gun.getAdsSpeed(itemStack) : gun.getSpeed(itemStack);
        addGunSpeedEffect(player, speed);

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

    @Shadow public abstract Iterable<ItemStack> getArmorItems();

    @Shadow public abstract void disableShield(boolean sprinting);

    @Shadow public abstract ItemCooldownManager getItemCooldownManager();

    public float applyBulletproofArmorToDamage(DamageSource source, float amount) {
        int bulletproofArmor = 0;

        if (!source.isIn(QTags.USES_BULLETPROOF_PROECTION_INSTEAD)) {
            return amount;
        }

        this.damageArmor(source, amount);

        for (ItemStack armorItemStack : this.getArmorItems()) {
            if (armorItemStack.getItem() instanceof ArmorItem armorItem) {
                if (armorItem.getMaterial() instanceof BulletproofArmorMaterial bulletproofArmorMaterial) {
                    bulletproofArmor = bulletproofArmor + bulletproofArmorMaterial.getBulletproofProtection(armorItem.getType());
                }
            }
        }

        if ((this.getInventory().getMainHandStack().getItem().equals(Items.SHIELD) || this.getEquippedStack(EquipmentSlot.OFFHAND).getItem().equals(Items.SHIELD)) && !this.getItemCooldownManager().isCoolingDown(Items.SHIELD)) {
            this.disableShield(true);
        }

        amount = DamageUtil.getDamageLeft(amount, bulletproofArmor, 0);

        return amount;
    }

    @ModifyVariable(method = "applyDamage", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float injectedNewDamageAmount(float original, DamageSource source, float amount) {
        return applyBulletproofArmorToDamage(source, original);
    }
}
