package queenofkelp.simplewarfare.gun;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;
import queenofkelp.simplewarfare.bullet.entity.BulletEntity;
import queenofkelp.simplewarfare.bullet.item.AmmoType;
import queenofkelp.simplewarfare.bullet.item.BulletItem;
import queenofkelp.simplewarfare.networking.QPackets;
import queenofkelp.simplewarfare.registry.QEntities;
import queenofkelp.simplewarfare.util.IEntityDataSaver;
import queenofkelp.simplewarfare.util.damage_dropoff.DamageDropoff;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Gun extends Item {

    protected Text name; //name of the weapon
    protected float damage;
    protected AmmoType ammoType;
    protected int ammo;
    protected int maxAmmo;
    protected int fireRate; //how many ticks before the gun shoots another bullet
    protected double velocity; //how many Blocks the bullets travel in one second
    protected float recoil; //the angle that the crossheir is pushed up every shot
    protected int penetration; //how many Blocks/Entities the projectile can hit (fragile Blocks like glass or bouncing)
    protected double penetrationMaxDropOff;
    protected int reloadTime; //how long it takes for the gun to reload (in ticks)
    protected int equipTime; //how long it takes to pull out a gun (in ticks) //TODO use mixin + player nbt instead of item nbt (maybe its kinda cool that you can hand someone a gun and it's ready to go)
    protected boolean isAutomatic;
    protected float bloom; //how much firing error there is (0 is perfect accuracy)
    protected DamageDropoff damageDropoff;
    protected GunSound shootSound;
    protected double movementInaccuracyMult;
    protected double maxMovementInnaccuracy;

    public static BulletEntity bulletEntity;
    public static World world;
    protected ArrayList<BulletItem> bulletsLoaded;


    public Gun(Settings settings, Text name, float damage, AmmoType ammoType, int maxAmmo, int ammo, int fireRate,
               double velocity, float recoil, float bloom, int penetration,
               int reloadTime, int equipTime, boolean isAutomatic, DamageDropoff damageDropoff, double penetrationMaxDropOff,
               GunSound shootSound, double movementInaccuracyMult, double maxMovementInnaccuracy) {
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
        this.movementInaccuracyMult = movementInaccuracyMult;
        this.maxMovementInnaccuracy = maxMovementInnaccuracy;
    }

    public void writeDefaultGunNbt(ItemStack gunItem) {
        NbtCompound gunNbt = gunItem.getOrCreateNbt();

        gunNbt.putInt("ammo", this.ammo);

        gunNbt.put("attachments", new NbtList());
    }

    public double getDamage() {
        return this.damage;
    }
    public boolean getIsAutomatic() {
        return this.isAutomatic;
    }

    public int getReloadTime() {
        return this.reloadTime;
    }

    public AmmoType getAmmoType() {
        return this.ammoType;
    }

    public int getMaxAmmo() {
        return this.maxAmmo;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        NbtCompound itemNbt = stack.getOrCreateNbt();

        if (itemNbt != null && itemNbt.get("ammo") == null) {

            writeDefaultGunNbt(stack);

            tooltip.add(Text.literal(
                            "Ammo:" + " (" + this.ammo + " / " + this.maxAmmo + ")")
                    .formatted(Formatting.RESET));
        } else {
            tooltip.add(Text.literal(
                            "Ammo:" + " (" + itemNbt.getInt("ammo") + " / " + this.maxAmmo + ")")
                    .formatted(Formatting.RESET));
        }

        tooltip.add(Text.literal(
                        "Damage: " + this.damage)
                .formatted(Formatting.RESET).fillStyle(Style.EMPTY.withColor(Formatting.RED))
        );

        tooltip.add(Text.literal(
                        "Fire Rate: " + 20 / this.fireRate + " rps")
                .formatted(Formatting.RESET).fillStyle(Style.EMPTY.withColor(Formatting.AQUA))
        );

        tooltip.add(Text.literal(
                        "Ammo Type: " + this.ammoType.displayName.getString())
                .formatted(Formatting.RESET).fillStyle(Style.EMPTY.withColor(Formatting.GOLD)));

        if (itemNbt.get("attachments") != null) {
            NbtList attachments = itemNbt.getList("attachments", 10);
            StringBuilder attachmentString = new StringBuilder();
            for (NbtElement e : attachments) {
                attachmentString.append(ItemStack.fromNbt((NbtCompound) e).getItem().getName().getString());
                attachmentString.append(", ");
            }

            tooltip.add(Text.literal(
                            "Attachments: " + attachmentString)
                    .formatted(Formatting.RESET).fillStyle(Style.EMPTY.withColor(Formatting.GREEN)));
        }

        tooltip.add(Text.literal(
                        "Penetration: " + this.penetration)
                .formatted(Formatting.RESET).fillStyle(Style.EMPTY.withColor(Formatting.BLUE)));

        tooltip.add(Text.literal(
                        "Bloom: " + this.bloom)
                .formatted(Formatting.RESET).fillStyle(Style.EMPTY.withColor(Formatting.LIGHT_PURPLE)));

        tooltip.add(Text.literal(
                        "Reload Time: " + this.reloadTime)
                .formatted(Formatting.RESET).fillStyle(Style.EMPTY.withColor(Formatting.YELLOW)));

    }

    public void inventoryTick(ItemStack itemStack, World world, Entity entity, int slot, boolean selected) {
        NbtCompound itemNbt = itemStack.getOrCreateNbt();

        if (selected && entity instanceof PlayerEntity user) {
            user.sendMessage(Text.literal(
                            this.name.getString() + " Ammo: (" + itemNbt.getInt("ammo") + " / " + this.maxAmmo + ")")
                    .formatted(Formatting.RESET), true);
        }

    }

    public void reload(PlayerEntity player, Hand hand, ItemStack item) {

        item.getNbt().putBoolean("reloading", false);

        NbtCompound itemNbt = item.getNbt();
        int totalBulletsAdded = 0;
        int totalBulletsRequired = itemNbt.getInt("maxAmmo") - itemNbt.getInt("ammo");
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack bulletStack = player.getInventory().getStack(i);
            if (bulletStack.getItem() instanceof BulletItem bullet && bullet.getBulletType() == this.ammoType) {
                if (bulletStack.getCount() >= totalBulletsRequired) {
                    totalBulletsAdded = totalBulletsRequired;
                    totalBulletsRequired = 0;
                    bulletStack.setCount(bulletStack.getCount() - totalBulletsAdded);
                } else {
                    totalBulletsAdded = bulletStack.getCount();
                    totalBulletsRequired = totalBulletsRequired - bulletStack.getCount();
                    bulletStack.setCount(0);
                }

                itemNbt.putInt("ammo", itemNbt.getInt("ammo") + totalBulletsAdded);
                totalBulletsAdded = 0;

                if (totalBulletsAdded >= totalBulletsRequired) {
                    player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.MASTER, .2f, 3f);
                    return;
                }
            }

        }
        player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.MASTER, .2f, 3f);


    }


    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 999;
    }

    public float getMovementInnacuracy(PlayerEntity user, float movementInnacuracyMult, float maxInnacuracy) {
        float movementInnacuracy = 0;
        NbtCompound nbt = ((IEntityDataSaver) user).getPersistentData();
        if (nbt.get("lastX") == null) {
            return 0;
        }

        movementInnacuracy = movementInnacuracy + (Math.abs((float) (user.getX() - nbt.getDouble("lastX"))) * movementInnacuracyMult);
        movementInnacuracy = movementInnacuracy + (Math.abs((float) (user.getZ() - nbt.getDouble("lastZ"))) * movementInnacuracyMult);

        if (movementInnacuracy > maxInnacuracy) {
            movementInnacuracy = maxInnacuracy;
        }

        return movementInnacuracy;
    }

    public void shoot(World world, PlayerEntity user, float pitch, float yaw) {

        BulletEntity bulletEntity = new BulletEntity(user, world, this.damage, this.fireRate,
                this.penetration, this.damageDropoff, this.penetrationMaxDropOff);

        float bloom = this.bloom;
        bloom = bloom + getMovementInnacuracy(user, (float) this.movementInaccuracyMult, (float) this.maxMovementInnaccuracy);

        if (user.isSneaking() || user.isCrawling()) {
            bloom = bloom - (bloom * .5f);
        }

        bulletEntity.setPos(user.getX(), user.getEyeY(), user.getZ());
        bulletEntity.setVelocity(user, pitch, yaw, 0.0F, (float) this.velocity, bloom);

        Gun.bulletEntity = bulletEntity;
        Gun.world = world;

        PacketByteBuf buf = PacketByteBufs.create();
        ServerPlayNetworking.send((ServerPlayerEntity) user, QPackets.S2C_SPAWN_TRACER, buf);

    }

    @Override
    public UseAction getUseAction(ItemStack item) {
        return UseAction.NONE;
    }

    public boolean onClicked(ItemStack gun, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (clickType == ClickType.RIGHT && slot.canTakePartial(player)) {

            NbtCompound nbt = gun.getNbt();
            if (nbt == null) {
                return false;
            }
            NbtList nbtList = nbt.getList("attachments", 10);

            if (otherStack.isEmpty()) {
                if (nbtList.isEmpty()) {
                    if (nbt.getInt("ammo") > 0) {
                        //cursorStackReference.set(new ItemStack(this.ammoType, nbt.getInt("ammo"))); TODO
                        nbt.putInt("ammo", 0);
                        return true;
                    }
                    return false;
                }
                cursorStackReference.set(ItemStack.fromNbt((NbtCompound) nbtList.get(0)));
                nbtList.remove(0);
                return true;
            } else if (otherStack.getItem() instanceof GunAttatchment) {
                if (nbtList.size() < 2) {
                    nbtList.add(otherStack.writeNbt(new NbtCompound()));
                    otherStack.setCount(0);
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    public void onFired(World world, LivingEntity shooter, ItemStack gun) {
        NbtCompound itemNbt = gun.getNbt();

        if (shooter instanceof PlayerEntity user && user.getMainHandStack() == gun) {

            /*
            if (itemNbt.getInt("ammo") <= 0) {
                return;
            }
             */


            /*
            if (user.getItemCooldownManager().isCoolingDown(this) || ((IEntityDataSaver) user).getPersistentData().getBoolean("reloading") ||
                    !(itemNbt.getBoolean("pulledOut"))) {
                return;
            }
             */
            if (user.getItemCooldownManager().isCoolingDown(this)) {
                return;
            }

            /*
            if (!this.isAutomatic && itemNbt.getBoolean("hasShot")) {
                return;
            }
             */

            itemNbt.putBoolean("hasShot", true);

            float pitch = user.getPitch();
            float yaw = user.getYaw();

            if (gun.getDamage() > gun.getMaxDamage() - 2) {
                user.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1f, 1f);
                gun.setCount(0);
            }

            if (!world.isClient) {

                /*
                if (itemStack.getCount() == 0) {
                    ItemEntity ie = new ItemEntity(user.getWorld(), user.getX(), user.getY(), user.getZ(),
                            new ItemStack(this.ammoType, itemNbt.getInt("ammo")));
                    user.getWorld().spawnEntity(ie);
                }
                 */

                if (itemNbt.getInt("ammo") <= 0) {
                    //return;
                }

                gun.setDamage(gun.getDamage() + 1);


                itemNbt.putInt("ammo", itemNbt.getInt("ammo") - 1);

                //user.damage(GunDamageSource.bullet(null, user), (float) this.getDamage());

                this.shoot(world, user, pitch, yaw);
                world.playSound(null, user.getBlockPos(), this.shootSound.shootSound, SoundCategory.MASTER, this.shootSound.volume, this.shootSound.pitch);

                user.getItemCooldownManager().set(this, this.fireRate);
                //recoil after shooting
                user.setPitch(user.getPitch() - this.recoil);
                //recoil packet
                PacketByteBuf buffer = PacketByteBufs.create();
                buffer.writeFloat(this.recoil);
                ServerPlayNetworking.send(Objects.requireNonNull(Objects.requireNonNull(user.getServer()).getPlayerManager().getPlayer(user.getUuid())), QPackets.S2C_DO_RECOIL, buffer);

            }
        }
    }

}
