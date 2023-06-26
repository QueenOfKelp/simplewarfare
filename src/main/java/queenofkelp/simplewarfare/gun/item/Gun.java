package queenofkelp.simplewarfare.gun.item;

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
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import queenofkelp.simplewarfare.bullet.entity.BulletEntity;
import queenofkelp.simplewarfare.bullet.item.AmmoType;
import queenofkelp.simplewarfare.bullet.item.BulletItem;
import queenofkelp.simplewarfare.networking.QPackets;
import queenofkelp.simplewarfare.util.damage_dropoff.DamageDropoff;
import queenofkelp.simplewarfare.util.gun.GunBloom;
import queenofkelp.simplewarfare.util.gun.GunSound;

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

    public void writeDefaultGunNbt(ItemStack gunItem) {
        NbtCompound gunNbt = gunItem.getOrCreateNbt();

        gunNbt.putInt("ammo", this.ammo);

        gunNbt.put("attachments", new NbtList());
    }



    public float getDamage() {
        return this.damage;
    }
    public int getFireRate() {
        return this.fireRate;
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
        NbtCompound itemNbt = stack.getOrCreateNbt();

        if (itemNbt != null && itemNbt.get("ammo") == null) {

            writeDefaultGunNbt(stack);

            tooltip.add(Text.literal(
                            "Ammo:" + " (" + this.ammo + " / " + this.getMaxAmmo() + ")")
                    .formatted(Formatting.RESET));
        } else {
            tooltip.add(Text.literal(
                            "Ammo:" + " (" + itemNbt.getInt("ammo") + " / " + this.getMaxAmmo() + ")")
                    .formatted(Formatting.RESET));
        }

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
                        "Penetration: " + this.getPenetration())
                .formatted(Formatting.RESET).fillStyle(Style.EMPTY.withColor(Formatting.BLUE)));

        tooltip.add(Text.literal(
                        "Bloom: " + this.getBloom().bloomDegrees)
                .formatted(Formatting.RESET).fillStyle(Style.EMPTY.withColor(Formatting.LIGHT_PURPLE)));

        tooltip.add(Text.literal(
                        "Reload Time: " + this.getReloadTime())
                .formatted(Formatting.RESET).fillStyle(Style.EMPTY.withColor(Formatting.YELLOW)));

    }

    public void inventoryTick(ItemStack itemStack, World world, Entity entity, int slot, boolean selected) {
        NbtCompound itemNbt = itemStack.getOrCreateNbt();

        if (selected && entity instanceof PlayerEntity user) {
            user.sendMessage(Text.literal(
                            this.name.getString() + " Ammo: (" + itemNbt.getInt("ammo") + " / " + this.getMaxAmmo() + ")")
                    .formatted(Formatting.RESET), true);
        }

    }

    protected BulletItem typeOfBulletLoaded;
    public void reload(PlayerEntity player, ItemStack item) {
        NbtCompound itemNbt = item.getNbt();
        int totalBulletsAdded = 0;
        int totalBulletsRequired = this.getMaxAmmo() - itemNbt.getInt("ammo");
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack bulletStack = player.getInventory().getStack(i);
            if (bulletStack.getItem() instanceof BulletItem bullet && bullet.getBulletType() == this.getAmmoType()) {
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

    public void shoot(World world, PlayerEntity user, float pitch, float yaw) {
        BulletEntity bulletEntity = new BulletEntity(user, world, this.getDamage(), this.getFireRate(),
                this.getPenetration(), this.getDamageDropOff(), this.getPenetrationMaxDropOff());

        bulletEntity.setPos(user.getX(), user.getEyeY(), user.getZ());

        System.out.print("\n Total Bloom: " + this.getBloom().getTotalBloom(user) + "\n");
        bulletEntity.setVelocity(user, pitch, yaw, 0.0F, this.getVelocity(), this.getBloom().getTotalBloom(user));
        world.spawnEntity(bulletEntity);
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

                this.shoot(world, user, pitch, yaw);
                world.playSound(null, user.getBlockPos(), this.shootSound.shootSound, SoundCategory.MASTER, this.shootSound.volume, this.shootSound.pitch);

                user.getItemCooldownManager().set(this, this.getFireRate());
                //recoil after shooting
                user.setPitch(user.getPitch() - this.getRecoil());
                //recoil packet
                PacketByteBuf buffer = PacketByteBufs.create();
                buffer.writeFloat(this.getRecoil());
                ServerPlayNetworking.send(Objects.requireNonNull(Objects.requireNonNull(user.getServer()).getPlayerManager().getPlayer(user.getUuid())), QPackets.S2C_DO_RECOIL, buffer);

            }
        }
    }

}
