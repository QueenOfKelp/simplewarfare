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
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import queenofkelp.simplewarfare.bullet.entity.BulletEntity;
import queenofkelp.simplewarfare.bullet.item.AmmoType;
import queenofkelp.simplewarfare.bullet.item.BulletItem;
import queenofkelp.simplewarfare.networking.QPackets;
import queenofkelp.simplewarfare.util.damage_dropoff.DamageDropoff;
import queenofkelp.simplewarfare.util.gun.GunBloom;
import queenofkelp.simplewarfare.util.gun.GunSound;
import queenofkelp.simplewarfare.util.gun.GunShooterUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Gun extends Item {

    protected Text name;
    protected float damage;
    protected AmmoType ammoType;
    protected int ammo;
    protected int maxAmmo;
    protected int fireRate; //how many ticks before the gun can shoot another bullet
    protected float velocity; //how fast the bullets travel
    protected float recoil;
    protected int penetration; //how many Blocks/Entities the projectile can hit
    protected double penetrationMaxDropOff; //the maximum damage reduction from penetrating through blocks/entities
    protected int reloadTime; //how long it takes for the gun to reload (in ticks)
    protected int equipTime; //how long it takes to pull out a gun (in ticks)
    protected boolean isAutomatic;
    protected GunBloom bloom; //how much firing error there is
    protected DamageDropoff damageDropoff; //the damage drop off from distance
    protected GunSound shootSound;


    public Gun(Settings settings, Text name, float damage, AmmoType ammoType, int maxAmmo, int ammo, int fireRate,
               float velocity, float recoil, GunBloom bloom, int penetration, double penetrationMaxDropOff,
               int reloadTime, int equipTime, boolean isAutomatic, DamageDropoff damageDropoff,
               GunSound shootSound) {
        super(settings);
        this.name = name;
        this.damage = damage;
        this.ammoType = ammoType;
        this.maxAmmo = maxAmmo;
        this.ammo = ammo;
        this.fireRate = fireRate;
        this.velocity = velocity;
        this.recoil = recoil;
        this.bloom = bloom;
        this.penetration = penetration;
        this.penetrationMaxDropOff = penetrationMaxDropOff;
        this.reloadTime = reloadTime;
        this.equipTime = equipTime;
        this.isAutomatic = isAutomatic;
        this.damageDropoff = damageDropoff;
        this.shootSound = shootSound;
    }

    public boolean canAttachmentBePutOnGun(ItemStack gunItem, ItemStack attachment) {
        return true;
    }

    public int getAmmo(ItemStack gunItem) {
        return gunItem.getOrCreateNbt().getInt("Ammo");
    }
    public void setAmmo(ItemStack gunItem, int ammo) {
        NbtCompound gunNbt = gunItem.getOrCreateNbt();
        gunNbt.putInt("Ammo", ammo);
    }
    public ArrayList<ItemStack> getAttachments(ItemStack gunItem) {
        NbtCompound gunNbt = gunItem.getOrCreateNbt();
        ArrayList<ItemStack> attachments = new ArrayList<>();
        NbtList nbtAttachments = gunNbt.getList("Attachments", 10);

        for (NbtElement element : nbtAttachments) {
            attachments.add(ItemStack.fromNbt((NbtCompound) element));
        }
        return attachments;
    };
    public boolean tryPutAttachment(ItemStack gunItem, ItemStack attachment) {
        if (!this.canAttachmentBePutOnGun(gunItem, attachment)) {
            return false;
        }
        NbtCompound gunNbt = gunItem.getOrCreateNbt();
        NbtList nbtAttachments = gunNbt.getList("Attachments", 10);

        nbtAttachments.add(attachment.writeNbt(new NbtCompound()));
        gunNbt.put("Attachments", nbtAttachments);

        return true;
    }
    public boolean tryRemoveAttachment(ItemStack gunItem, ItemStack attachment) {
        NbtCompound gunNbt = gunItem.getOrCreateNbt();
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
    public ItemStack removeTopGunAttachment(ItemStack gunItem) {
        NbtCompound gunNbt = gunItem.getOrCreateNbt();
        NbtList nbtAttachments = gunNbt.getList("Attachments", 10);

        if (nbtAttachments.isEmpty()) {
            return null;
        }

        NbtElement attachmentNbt = nbtAttachments.get(0);
        nbtAttachments.remove(attachmentNbt);

        gunNbt.put("Attachments", nbtAttachments);

        return ItemStack.fromNbt((NbtCompound) attachmentNbt);
    }
    public Item getBulletItemLoaded(ItemStack gunItem) {
        NbtCompound gunNbt = gunItem.getOrCreateNbt();

        if (gunNbt.getString("BulletLoadedID").equals("")) {
            return null;
        }

        return Registries.ITEM.get(new Identifier(gunNbt.getString("BulletLoadedID")));
    }
    public void setBulletItemLoaded(ItemStack gunItem, @Nullable Item itemToLoad) {
        NbtCompound gunNbt = gunItem.getOrCreateNbt();

        if (itemToLoad == null) {
            gunNbt.putString("BulletLoadedID", "");
        }
        else {
            gunNbt.putString("BulletLoadedID", Registries.ITEM.getId(itemToLoad).toString());
        }
    }

    public float getDamage() {
        return this.damage;
    }
    public int getFireRate() {
        return this.fireRate;
    }
    public int getEquipTime() {
        return this.equipTime;
    }
    public float getVelocity() {
        return this.velocity;
    }
    public float getRecoil() {
        return this.recoil;
    }
    public GunBloom getBloom() {
        return this.bloom;
    }
    public int getPenetration() {
        return this.penetration;
    }
    public double getPenetrationMaxDropOff() {
        return this.penetrationMaxDropOff;
    }
    public DamageDropoff getDamageDropOff() {
        return this.damageDropoff;
    }
    public GunSound getShootSound() {
        return this.shootSound;
    }
    public boolean getIsAutomatic() {
        return this.isAutomatic;
    }

    public int getReloadTime() {
        return this.reloadTime;
    }

    public int getMaxAmmo() {
        return this.maxAmmo;
    }
    public AmmoType getAmmoType() {
        return this.ammoType;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal(
                        "Ammo:" + " (" + this.getAmmo(stack) + " / " + this.getMaxAmmo() + ")")
                .formatted(Formatting.RESET));

        tooltip.add(Text.literal(
                        "Damage: " + this.getDamage())
                .formatted(Formatting.RESET).fillStyle(Style.EMPTY.withColor(Formatting.RED))
        );

        tooltip.add(Text.literal(
                        "Fire Rate: " + 20 / this.getFireRate() + " rps")
                .formatted(Formatting.RESET).fillStyle(Style.EMPTY.withColor(Formatting.AQUA))
        );

        tooltip.add(Text.literal(
                        "Ammo Type: " + this.getAmmoType().displayName.getString())
                .formatted(Formatting.RESET).fillStyle(Style.EMPTY.withColor(Formatting.GOLD)));

        if (!this.getAttachments(stack).isEmpty()) {
            ArrayList<ItemStack> attachments = this.getAttachments(stack);
            StringBuilder attachmentString = new StringBuilder();
            for (ItemStack attachment : attachments) {
                attachmentString.append(attachment.getItem().getName().getString()).append("x").append(attachment.getCount()).append(", ");
            }

            tooltip.add(Text.literal(
                            "Attachments: " + attachmentString)
                    .formatted(Formatting.RESET).fillStyle(Style.EMPTY.withColor(Formatting.GREEN)));
        }

        tooltip.add(Text.literal(
                        "Penetration: " + this.getPenetration() + " Max Damage Reduction From Penetration: " + this.getPenetrationMaxDropOff())
                .formatted(Formatting.RESET).fillStyle(Style.EMPTY.withColor(Formatting.BLUE)));

        tooltip.add(Text.literal(
                        "Bloom: " + this.getBloom().bloomDegrees)
                .formatted(Formatting.RESET).fillStyle(Style.EMPTY.withColor(Formatting.LIGHT_PURPLE)));

        tooltip.add(Text.literal(
                        "Reload Time: " + this.getReloadTime())
                .formatted(Formatting.RESET).fillStyle(Style.EMPTY.withColor(Formatting.YELLOW)));
        tooltip.add(Text.literal(
                        "Distance Dropoff: " + this.getDamageDropOff().getDisplayInformation())
                .formatted(Formatting.RESET).fillStyle(Style.EMPTY.withColor(Formatting.DARK_RED)));

    }

    public void inventoryTick(ItemStack itemStack, World world, Entity entity, int slot, boolean selected) {
        if (selected && entity instanceof PlayerEntity user) {
            user.sendMessage(Text.literal(
                            this.name.getString() + " Ammo: (" + getAmmo(itemStack) + " / " + this.getMaxAmmo() + ")")
                    .formatted(Formatting.RESET), true);
        }
    }

    public void reload(PlayerEntity player, ItemStack gunItem) {
        this.playReloadFinishSound(player, gunItem);

        int bulletsAddedFromStack;
        int totalBulletsRequired = this.getMaxAmmo() - this.getAmmo(gunItem);
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack bulletStack = player.getInventory().getStack(i);
            if (bulletStack.getItem() instanceof BulletItem bullet && bullet.getBulletType() == this.getAmmoType() &&
                    (this.getBulletItemLoaded(gunItem) == null || this.getBulletItemLoaded(gunItem).equals(bullet))) {
                this.setBulletItemLoaded(gunItem, bullet);
                if (bulletStack.getCount() >= totalBulletsRequired) {
                    bulletsAddedFromStack = totalBulletsRequired;
                    totalBulletsRequired = 0;
                    bulletStack.setCount(bulletStack.getCount() - bulletsAddedFromStack);
                } else {
                    bulletsAddedFromStack = bulletStack.getCount();
                    totalBulletsRequired = totalBulletsRequired - bulletStack.getCount();
                    bulletStack.setCount(0);
                }

                this.setAmmo(gunItem, this.getAmmo(gunItem) + bulletsAddedFromStack);
                bulletsAddedFromStack = 0;

                if (bulletsAddedFromStack >= totalBulletsRequired) {
                    break;
                }
            }

        }
    }

    public void playReloadFinishSound(PlayerEntity player, ItemStack gunItem) {
        player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.MASTER, .2f, 3f);
    }
    public void playReloadStartSound(PlayerEntity player, ItemStack gunItem) {
        player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_HORSE_GALLOP, SoundCategory.MASTER,
                1f, 1f/(this.getReloadTime()*2));
    }

    public void shoot(World world, PlayerEntity user, float pitch, float yaw, ItemStack gunItem) {
        BulletEntity bulletEntity = new BulletEntity(user, world, this.getDamage(), this.getFireRate(),
                this.getPenetration(), this.getDamageDropOff(), this.getPenetrationMaxDropOff());

        bulletEntity.setPos(user.getX(), user.getEyeY(), user.getZ());

        System.out.print("\n Total Bloom: " + this.getBloom().getTotalBloom(user) + "\n");
        bulletEntity.setVelocity(user, pitch, yaw, 0.0F, this.getVelocity(), this.getBloom().getTotalBloom(user));
        world.spawnEntity(bulletEntity);
    }

    public void checkResetBulletLoaded(ItemStack gunItem) {
        if (this.getAmmo(gunItem) <= 0) {
            this.setBulletItemLoaded(gunItem, null);
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

    public void playShootSound(BlockPos blockPos, World world) {
        world.playSound(null, blockPos, this.shootSound.shootSound, SoundCategory.MASTER, this.shootSound.volume, this.shootSound.pitch);
    }

    public void doRecoil(PlayerEntity shooter) {
        shooter.setPitch(shooter.getPitch() - this.getRecoil());
        //recoil packet
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeFloat(this.getRecoil());
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
                this.playShootSound(shooter.getBlockPos(), world);

                shooter.getItemCooldownManager().set(this, this.getFireRate());

                this.doRecoil(shooter);
            }
        }
    }

}
