package queenofkelp.simplewarfare.gun.item;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import queenofkelp.simplewarfare.bullet.entity.BulletEntity;
import queenofkelp.simplewarfare.bullet.item.AmmoType;
import queenofkelp.simplewarfare.bullet.item.BulletItem;
import queenofkelp.simplewarfare.gun.item.attachments.AbstractStatModifyingAttachmentItem;
import queenofkelp.simplewarfare.gun.item.attachments.GunAttachmentItem;
import queenofkelp.simplewarfare.networking.QPackets;
import queenofkelp.simplewarfare.util.damage_dropoff.DamageDropoff;
import queenofkelp.simplewarfare.util.gun.GunBloom;
import queenofkelp.simplewarfare.util.gun.GunSound;
import queenofkelp.simplewarfare.util.gun.GunShooterUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Gun extends Item {
    public float damage;
    public AmmoType ammoType;
    public int ammo;
    public int maxAmmo;
    public int fireRate; //how many ticks before the gun can shoot another bullet
    public float velocity; //how fast the bullets travel
    public float recoil;
    public int penetration; //how many Blocks/Entities the projectile can hit
    public double penetrationMaxDropOff; //the maximum damage reduction from penetrating through blocks/entities
    public int reloadTime; //how long it takes for the gun to reload (in ticks)
    public int equipTime; //how long it takes to pull out a gun (in ticks)
    public boolean isAutomatic;
    public GunBloom bloom; //how much firing error there is
    public DamageDropoff damageDropoff; //the damage drop off from distance
    public GunSound shootSound;
    public float adsFovMult;
    public float Speed;
    public float adsSpeed;

    public Gun(Settings settings, float damage, AmmoType ammoType, int maxAmmo, int ammo, int fireRate,
               float velocity, float recoil, GunBloom bloom, float adsFovMult, float Speed, float adsSpeed,
               int penetration, double penetrationMaxDropOff, int reloadTime, int equipTime, boolean isAutomatic,
               DamageDropoff damageDropoff, GunSound shootSound) {
        super(settings);
        this.damage = damage;
        this.ammoType = ammoType;
        this.maxAmmo = maxAmmo;
        this.ammo = ammo;
        this.fireRate = fireRate;
        this.velocity = velocity;
        this.recoil = recoil;
        this.bloom = bloom;
        this.adsFovMult = adsFovMult;
        this.Speed = Speed;
        this.adsSpeed = adsSpeed;
        this.penetration = penetration;
        this.penetrationMaxDropOff = penetrationMaxDropOff;
        this.reloadTime = reloadTime;
        this.equipTime = equipTime;
        this.isAutomatic = isAutomatic;
        this.damageDropoff = damageDropoff;
        this.shootSound = shootSound;
    }

    public boolean canAttachmentBePutOnGun(ItemStack gun, ItemStack attachment) {
        return true;
    }

    public int getAmmo(ItemStack gun) {
        return gun.getOrCreateNbt().getInt("Ammo");
    }
    public void setAmmo(ItemStack gun, int ammo) {
        NbtCompound gunNbt = gun.getOrCreateNbt();
        gunNbt.putInt("Ammo", ammo);
    }
    public ArrayList<ItemStack> getAttachments(ItemStack gun) {
        NbtCompound gunNbt = gun.getOrCreateNbt();
        ArrayList<ItemStack> attachments = new ArrayList<>();
        NbtList nbtAttachments = gunNbt.getList("Attachments", 10);

        for (NbtElement element : nbtAttachments) {
            attachments.add(ItemStack.fromNbt((NbtCompound) element));
        }
        return attachments;
    };
    public boolean tryPutAttachment(ItemStack gun, ItemStack attachment) {
        if (!this.canAttachmentBePutOnGun(gun, attachment)) {
            return false;
        }
        NbtCompound gunNbt = gun.getOrCreateNbt();
        NbtList nbtAttachments = gunNbt.getList("Attachments", 10);

        nbtAttachments.add(attachment.writeNbt(new NbtCompound()));
        gunNbt.put("Attachments", nbtAttachments);

        return true;
    }
    public boolean tryRemoveAttachment(ItemStack gun, ItemStack attachment) {
        NbtCompound gunNbt = gun.getOrCreateNbt();
        NbtList nbtAttachments = gunNbt.getList("Attachments", 10);

        for (NbtElement element : nbtAttachments) {
            if (element.equals(attachment.getNbt())) {
                nbtAttachments.remove(element);
                gunNbt.put("Attachments", nbtAttachments);
                return true;
            }
        }
        return false;
    }
    public ItemStack removeTopGunAttachment(ItemStack gun) {
        NbtCompound gunNbt = gun.getOrCreateNbt();
        NbtList nbtAttachments = gunNbt.getList("Attachments", 10);

        if (nbtAttachments.isEmpty()) {
            return null;
        }

        NbtElement attachmentNbt = nbtAttachments.get(0);
        nbtAttachments.remove(attachmentNbt);

        gunNbt.put("Attachments", nbtAttachments);

        return ItemStack.fromNbt((NbtCompound) attachmentNbt);
    }
    public BulletItem getBulletItemLoaded(ItemStack gun) {
        NbtCompound gunNbt = gun.getOrCreateNbt();

        if (gunNbt.getString("BulletLoadedID").equals("")) {
            return null;
        }

        return (BulletItem) Registries.ITEM.get(new Identifier(gunNbt.getString("BulletLoadedID")));
    }
    public void setBulletItemLoaded(ItemStack gun, @Nullable Item itemToLoad) {
        NbtCompound gunNbt = gun.getOrCreateNbt();

        if (itemToLoad == null) {
            gunNbt.putString("BulletLoadedID", "");
        }
        else {
            gunNbt.putString("BulletLoadedID", Registries.ITEM.getId(itemToLoad).toString());
        }
    }

    public float getDamage(ItemStack gun) {
        float damage = this.damage;

        for (ItemStack attachment : this.getAttachments(gun)) {
            if (attachment.getItem() instanceof AbstractStatModifyingAttachmentItem statAttachment) {
                damage = statAttachment.modifyDamage(damage);
            }
        }

        return damage;
    }
    public int getFireRate(ItemStack gun) {
        int fireRate = this.fireRate;

        for (ItemStack attachment : this.getAttachments(gun)) {
            if (attachment.getItem() instanceof AbstractStatModifyingAttachmentItem statAttachment) {
                fireRate = statAttachment.modifyFireRate(fireRate);
            }
        }

        return fireRate;
    }
    public int getEquipTime(ItemStack gun) {
        int equipTime = this.equipTime;

        for (ItemStack attachment : this.getAttachments(gun)) {
            if (attachment.getItem() instanceof AbstractStatModifyingAttachmentItem statAttachment) {
                equipTime = statAttachment.modifyEquipTime(equipTime);
            }
        }

        return equipTime;
    }
    public float getVelocity(ItemStack gun) {
        float velocity = this.velocity;

        for (ItemStack attachment : this.getAttachments(gun)) {
            if (attachment.getItem() instanceof AbstractStatModifyingAttachmentItem statAttachment) {
                velocity = statAttachment.modifyVelocity(velocity);
            }
        }

        return velocity;
    }
    public float getRecoil(ItemStack gun) {
        float recoil = this.recoil;

        for (ItemStack attachment : this.getAttachments(gun)) {
            if (attachment.getItem() instanceof AbstractStatModifyingAttachmentItem statAttachment) {
                recoil = statAttachment.modifyRecoil(recoil);
            }
        }

        return recoil;
    }
    public GunBloom getBloom(ItemStack gun) {
        GunBloom bloom = this.bloom;

        for (ItemStack attachment : this.getAttachments(gun)) {
            if (attachment.getItem() instanceof AbstractStatModifyingAttachmentItem statAttachment) {
                bloom = statAttachment.modifyBloom(bloom);
            }
        }

        return bloom;
    }
    public float getAdsFovMult(ItemStack gun) {
        float adsFovMult = this.adsFovMult;

        for (ItemStack attachment : this.getAttachments(gun)) {
            if (attachment.getItem() instanceof AbstractStatModifyingAttachmentItem statAttachment) {
                adsFovMult = statAttachment.modifyAdsFovMult(adsFovMult);
            }
        }

        return adsFovMult;
    }
    public float getSpeed(ItemStack gun) {
        float Speed = this.Speed;

        for (ItemStack attachment : this.getAttachments(gun)) {
            if (attachment.getItem() instanceof AbstractStatModifyingAttachmentItem statAttachment) {
                Speed = statAttachment.modifySpeed(Speed);
            }
        }

        return Speed;
    }
    public float getAdsSpeed(ItemStack gun) {
        float Speed = this.adsSpeed;

        for (ItemStack attachment : this.getAttachments(gun)) {
            if (attachment.getItem() instanceof AbstractStatModifyingAttachmentItem statAttachment) {
                Speed = statAttachment.modifyAdsSpeed(Speed);
            }
        }

        return Speed;
    }
    public int getPenetration(ItemStack gun) {
        int penetration = this.penetration;

        for (ItemStack attachment : this.getAttachments(gun)) {
            if (attachment.getItem() instanceof AbstractStatModifyingAttachmentItem statAttachment) {
                penetration = statAttachment.modifyPenetration(penetration);
            }
        }

        return penetration;
    }
    public double getPenetrationMaxDropOff(ItemStack gun) {
        double penetrationMaxDropOff = this.penetrationMaxDropOff;

        for (ItemStack attachment : this.getAttachments(gun)) {
            if (attachment.getItem() instanceof AbstractStatModifyingAttachmentItem statAttachment) {
                penetrationMaxDropOff = statAttachment.modifyMaxPenetrationDropoff(penetrationMaxDropOff);
            }
        }

        return penetrationMaxDropOff;
    }
    public DamageDropoff getDamageDropOff(ItemStack gun) {
        DamageDropoff damageDropoff = this.damageDropoff;

        for (ItemStack attachment : this.getAttachments(gun)) {
            if (attachment.getItem() instanceof AbstractStatModifyingAttachmentItem statAttachment) {
                damageDropoff = statAttachment.modifyDamageDropoff(damageDropoff);
            }
        }

        return damageDropoff;
    }
    public GunSound getShootSound(ItemStack gun) {
        GunSound shootSound = this.shootSound;

        for (ItemStack attachment : this.getAttachments(gun)) {
            if (attachment.getItem() instanceof AbstractStatModifyingAttachmentItem statAttachment) {
                shootSound = statAttachment.modifyGunSound(shootSound);
            }
        }

        return shootSound;
    }
    public boolean getIsAutomatic(ItemStack gun) {
        boolean isAutomatic = this.isAutomatic;

        for (ItemStack attachment : this.getAttachments(gun)) {
            if (attachment.getItem() instanceof AbstractStatModifyingAttachmentItem statAttachment) {
                isAutomatic = statAttachment.modifyIsAutomatic(isAutomatic);
            }
        }

        return isAutomatic;
    }

    public int getReloadTime(ItemStack gun) {
        int reloadTime = this.reloadTime;

        for (ItemStack attachment : this.getAttachments(gun)) {
            if (attachment.getItem() instanceof AbstractStatModifyingAttachmentItem statAttachment) {
                reloadTime = statAttachment.modifyReloadTime(reloadTime);
            }
        }

        return reloadTime;
    }

    public int getMaxAmmo(ItemStack gun) {
        int maxAmmo = this.maxAmmo;

        for (ItemStack attachment : this.getAttachments(gun)) {
            if (attachment.getItem() instanceof AbstractStatModifyingAttachmentItem statAttachment) {
                maxAmmo = statAttachment.modifyMaxAmmo(maxAmmo);
            }
        }

        return maxAmmo;
    }
    public AmmoType getAmmoType(ItemStack gun) {
        return this.ammoType;
    }

    public boolean gunHasDefaultAnimations() {
        return true;
    }

    @Override
    public void appendTooltip(ItemStack gun, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal(
                        "Ammo:" + " (" + this.getAmmo(gun) + " / " + this.getMaxAmmo(gun) + ")")
                .formatted(Formatting.RESET));

        tooltip.add(Text.literal(
                        "Damage: " + this.getDamage(gun))
                .formatted(Formatting.RESET).fillStyle(Style.EMPTY.withColor(Formatting.RED))
        );

        tooltip.add(Text.literal(
                        "Fire Rate: " + 20 / this.getFireRate(gun) + " rps")
                .formatted(Formatting.RESET).fillStyle(Style.EMPTY.withColor(Formatting.AQUA))
        );

        tooltip.add(Text.literal(
                        "Ammo Type: " + this.getAmmoType(gun).displayName.getString())
                .formatted(Formatting.RESET).fillStyle(Style.EMPTY.withColor(Formatting.GOLD)));

        if (!this.getAttachments(gun).isEmpty()) {
            ArrayList<ItemStack> attachments = this.getAttachments(gun);
            StringBuilder attachmentString = new StringBuilder();
            for (ItemStack attachment : attachments) {
                attachmentString.append(attachment.getItem().getName().getString()).append("x").append(attachment.getCount()).append(", ");
            }

            tooltip.add(Text.literal(
                            "Attachments: " + attachmentString)
                    .formatted(Formatting.RESET).fillStyle(Style.EMPTY.withColor(Formatting.GREEN)));
        }

        tooltip.add(Text.literal(
                        "Penetration: " + this.getPenetration(gun) + " Max Damage Reduction From Penetration: " + this.getPenetrationMaxDropOff(gun))
                .formatted(Formatting.RESET).fillStyle(Style.EMPTY.withColor(Formatting.BLUE)));

        tooltip.add(Text.literal(
                        "Bloom: " + this.getBloom(gun).bloomDegrees)
                .formatted(Formatting.RESET).fillStyle(Style.EMPTY.withColor(Formatting.LIGHT_PURPLE)));

        tooltip.add(Text.literal(
                        "Reload Time: " + this.getReloadTime(gun))
                .formatted(Formatting.RESET).fillStyle(Style.EMPTY.withColor(Formatting.YELLOW)));
        tooltip.add(Text.literal(
                        "Distance Dropoff: " + this.getDamageDropOff(gun).getDisplayInformation())
                .formatted(Formatting.RESET).fillStyle(Style.EMPTY.withColor(Formatting.DARK_RED)));

    }

    public void inventoryTick(ItemStack gun, World world, Entity entity, int slot, boolean selected) {
        if (selected && entity instanceof PlayerEntity user) {
            user.sendMessage(Text.literal(
                            this.getName().getString() + " Ammo: (" + getAmmo(gun) + " / " + this.getMaxAmmo(gun) + ")")
                    .formatted(Formatting.RESET), true);
        }
    }

    public void reload(PlayerEntity player, ItemStack gun) {
        this.playReloadFinishSound(player, gun);

        int bulletsAddedFromStack;
        int totalBulletsRequired = this.getMaxAmmo(gun) - this.getAmmo(gun);
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack bulletStack = player.getInventory().getStack(i);
            if (bulletStack.getItem() instanceof BulletItem bullet && bullet.getBulletType() == this.getAmmoType(gun) &&
                    (this.getBulletItemLoaded(gun) == null || this.getBulletItemLoaded(gun).equals(bullet))) {
                this.setBulletItemLoaded(gun, bullet);
                if (bulletStack.getCount() >= totalBulletsRequired) {
                    bulletsAddedFromStack = totalBulletsRequired;
                    totalBulletsRequired = 0;
                    bulletStack.setCount(bulletStack.getCount() - bulletsAddedFromStack);
                } else {
                    bulletsAddedFromStack = bulletStack.getCount();
                    totalBulletsRequired = totalBulletsRequired - bulletStack.getCount();
                    bulletStack.setCount(0);
                }

                this.setAmmo(gun, this.getAmmo(gun) + bulletsAddedFromStack);
                bulletsAddedFromStack = 0;

                if (bulletsAddedFromStack >= totalBulletsRequired) {
                    break;
                }
            }

        }
    }

    public void playReloadFinishSound(PlayerEntity player, ItemStack gun) {
        player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.MASTER, .2f, 3f);
    }
    public void playReloadStartSound(PlayerEntity player, ItemStack gun) {
        player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_HORSE_GALLOP, SoundCategory.MASTER,
                1f, 1f/(this.getReloadTime(gun)*2));
    }

    public void shoot(World world, PlayerEntity user, float pitch, float yaw, ItemStack gun) {
        BulletEntity bulletEntity = this.getBulletItemLoaded(gun).createBulletForItem(user, world, this.getDamage(gun), this.getFireRate(gun),
                this.getPenetration(gun), this.getDamageDropOff(gun), this.getPenetrationMaxDropOff(gun), new Vec3d(user.getX(), user.getEyeY(), user.getZ()),
                this.getBloom(gun).getTotalBloom(user), this.getVelocity(gun), pitch, yaw);

        world.spawnEntity(bulletEntity);
    }

    public void checkResetBulletLoaded(ItemStack gun) {
        if (this.getAmmo(gun) <= 0) {
            this.setBulletItemLoaded(gun, null);
        }
    }

    public boolean onClicked(ItemStack gun, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (clickType == ClickType.RIGHT && slot.canTakePartial(player)) {
            if (otherStack.isEmpty()) {
                if (this.getAttachments(gun).isEmpty()) {
                    if (this.getAmmo(gun) > 0) {
                        cursorStackReference.set(new ItemStack(this.getBulletItemLoaded(gun), this.getAmmo(gun)));
                        this.setAmmo(gun, 0);
                        return true;
                    }
                    return false;
                }
                cursorStackReference.set(this.removeTopGunAttachment(gun));
                return true;
            } else if (otherStack.getItem() instanceof GunAttachmentItem) {
                if (this.tryPutAttachment(gun, otherStack)) {
                    otherStack.setCount(0);
                    return true;
                }
                return false;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void playShootSound(BlockPos blockPos, World world, ItemStack gun) {
        world.playSound(null, blockPos, this.getShootSound(gun).shootSound, SoundCategory.MASTER, this.getShootSound(gun).volume, this.getShootSound(gun).pitch);
    }

    public void doRecoil(PlayerEntity shooter, ItemStack gun) {
        shooter.setPitch(shooter.getPitch() - this.getRecoil(gun));
        //recoil packet
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeFloat(this.getRecoil(gun));
        ServerPlayNetworking.send(Objects.requireNonNull(Objects.requireNonNull(shooter.getServer()).getPlayerManager().getPlayer(shooter.getUuid())), QPackets.S2C_DO_RECOIL, buffer);
    }

    public void onFired(World world, PlayerEntity shooter, ItemStack gun) {
        if (shooter.getMainHandStack() == gun) {
            boolean canShoot = (this.getAmmo(gun) > 0) && (GunShooterUtil.getPlayerReloadTime(shooter) <= 0)
                    && !shooter.getItemCooldownManager().isCoolingDown(this);
            if (!canShoot) {
                return;
            }

            gun.setDamage(gun.getDamage() + 1);

            if (gun.getDamage() > gun.getMaxDamage()) {
                shooter.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1f, 1f);
                gun.setCount(0);
            }

            if (!world.isClient) {
                if (gun.getCount() == 0) {
                    ItemEntity ie = new ItemEntity(shooter.getWorld(), shooter.getX(), shooter.getY(), shooter.getZ(),
                            new ItemStack(this.getBulletItemLoaded(gun), this.getAmmo(gun)));
                    shooter.getWorld().spawnEntity(ie);
                }

                this.setAmmo(gun, this.getAmmo(gun) - 1);

                this.shoot(world, shooter, shooter.getPitch(), shooter.getYaw(), gun);
                this.playShootSound(shooter.getBlockPos(), world, gun);

                shooter.getItemCooldownManager().set(this, this.getFireRate(gun));

                this.doRecoil(shooter, gun);
            }
        }
    }

}
