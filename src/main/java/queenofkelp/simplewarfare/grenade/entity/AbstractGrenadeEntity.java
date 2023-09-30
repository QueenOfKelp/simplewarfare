package queenofkelp.simplewarfare.grenade.entity;

import com.google.common.collect.ImmutableList;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import org.jetbrains.annotations.Nullable;
import queenofkelp.simplewarfare.mixin.CollisionsAccessorMixin;
import queenofkelp.simplewarfare.networking.QPackets;
import queenofkelp.simplewarfare.util.CollisionsAccessor;

import java.util.List;

public abstract class AbstractGrenadeEntity extends PersistentProjectileEntity implements FlyingItemEntity {

    protected int startFuse;
    protected int fuse;

    protected double frictionAmount;

    protected boolean explodeOnImpact;

    protected double bounceAmount;


    public AbstractGrenadeEntity(EntityType<? extends AbstractGrenadeEntity> entityType, World world) {
        super(entityType, world);
    }

    public AbstractGrenadeEntity(EntityType<? extends AbstractGrenadeEntity> entityType, LivingEntity owner, World world,
                                 int startFuse, double frictionAmount, boolean explodeOnImpact, double bounceAmount) {
        super(entityType, world);

        this.startFuse = startFuse;
        this.fuse = startFuse;
        this.frictionAmount = frictionAmount;
        this.explodeOnImpact = explodeOnImpact;
        this.bounceAmount = bounceAmount;
        this.setOwner(owner);
    }

    public AbstractGrenadeEntity(EntityType<? extends AbstractGrenadeEntity> entityType, double x, double y, double z, World world) {
        super(entityType, world);
        this.setPos(x, y, z);
    }

    public int getFuse() {
        return this.fuse;
    }
    public void setFuse(int fuse) {
        this.fuse = fuse;
    }
    protected double getFrictionAmount() {
        return this.frictionAmount;
    }
    protected boolean isExplodeOnImpact() {
        return this.explodeOnImpact;
    }
    protected double getBounceAmount() {
        return this.bounceAmount;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        try {
            nbt.putDouble("GrenadeBounceAmount", this.bounceAmount);
            nbt.putInt("GrenadeFuse", this.fuse);
            nbt.putInt("GrenadeStartFuse", this.startFuse);
            nbt.putDouble("GrenadeFrictionAmount", this.frictionAmount);
            nbt.putBoolean("GrenadeExplodeOnImpact", this.explodeOnImpact);
        } catch (Throwable var9) {
            CrashReport crashReport = CrashReport.create(var9, "Saving entity NBT");
            CrashReportSection crashReportSection = crashReport.addElement("Entity being saved");
            this.populateCrashReport(crashReportSection);
            throw new CrashException(crashReport);
        }
        super.writeNbt(nbt);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        try {
            this.bounceAmount = nbt.getDouble("GrenadeBounceAmount");
            this.fuse = nbt.getInt("GrenadeFuse");
            this.startFuse = nbt.getInt("GrenadeStartFuse");
            this.frictionAmount = nbt.getDouble("GrenadeFrictionAmount");
            this.explodeOnImpact = nbt.getBoolean("GrenadeExplodeOnImpact");
        } catch (Throwable var17) {
            CrashReport crashReport = CrashReport.create(var17, "Loading entity NBT");
            CrashReportSection crashReportSection = crashReport.addElement("Entity being loaded");
            this.populateCrashReport(crashReportSection);
            throw new CrashException(crashReport);
        }
        super.readNbt(nbt);
    }

    protected ItemStack getItem() {
        return null;
    }

    protected void onEntityHit(EntityHitResult entityHitResult) { // called on entity hit.
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        if (!this.world.isClient) {
            if (this.isExplodeOnImpact()) {
                this.explode();
            }
        }
    }

    protected void onCollision(HitResult hitResult) { // called on collision with a block
        super.onCollision(hitResult);
        if (!this.world.isClient) { // checks if the world is client
            this.world.sendEntityStatus(this, (byte)3); // particle?
            if (this.isExplodeOnImpact()) {
                this.explode();
            }
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                Vec3d oldVelocity = this.getVelocity();
                BlockHitResult blockHitResult = (BlockHitResult) hitResult;

                this.bounce(this.getBounceAmount(), blockHitResult.getSide(), oldVelocity);
            }
        }
    }

    protected void bounce(double amount, Direction direction) {
        if (direction.equals(Direction.NORTH) || direction.equals(Direction.SOUTH)) {
            this.setVelocity(this.getVelocity().getX(), this.getVelocity().getY(), this.getVelocity().getZ() * (-amount));
        }
        else if (direction.equals(Direction.EAST) || direction.equals(Direction.WEST)) {
            this.setVelocity(this.getVelocity().getX() * (-amount), this.getVelocity().getY(), this.getVelocity().getZ());

        }
        else if (direction.equals(Direction.UP) || direction.equals(Direction.DOWN)) {
            this.setVelocity(this.getVelocity().getX(), this.getVelocity().getY() * (-amount), this.getVelocity().getZ());
        }
    }

    protected void friction(double amount) {
        if (this.getVelocity().getX() > 0) {
            this.setVelocity(this.getVelocity().getX() - amount, this.getVelocity().getY(), this.getVelocity().getZ());
        }
        else if (this.getVelocity().getX() < 0) {
            this.setVelocity(this.getVelocity().getX() + amount, this.getVelocity().getY(), this.getVelocity().getZ());
        }
        if (this.getVelocity().getZ() > 0) {
            this.setVelocity(this.getVelocity().getX(), this.getVelocity().getY(), this.getVelocity().getZ() - amount);
        }
        else if (this.getVelocity().getZ() < 0) {
            this.setVelocity(this.getVelocity().getX(), this.getVelocity().getY(), this.getVelocity().getZ() + amount);
        }
    }

    protected void bounce(double amount, Direction direction, Vec3d startingVelocity) {
        if (direction.equals(Direction.NORTH) || direction.equals(Direction.SOUTH)) {
            this.setVelocity(startingVelocity.getX(), startingVelocity.getY(), startingVelocity.getZ() * (-amount));
        }
        else if (direction.equals(Direction.EAST) || direction.equals(Direction.WEST)) {
            this.setVelocity(startingVelocity.getX() * (-amount), startingVelocity.getY(), startingVelocity.getZ());
        }
        else if (direction.equals(Direction.UP) || direction.equals(Direction.DOWN)) {
            this.setVelocity(startingVelocity.getX(), startingVelocity.getY() * (-amount), startingVelocity.getZ());
        }
    }

    protected void explode() {
        this.discard();
    }

    protected void spawnGrenadeParticles() {
        for (Entity e : this.getWorld().getOtherEntities(null, this.getBoundingBox().expand(50))) {
            if (e instanceof ServerPlayerEntity sp) {
                ServerPlayNetworking.send(sp, QPackets.S2C_SPAWN_PARTICLE, QPackets.makeSpawnParticlesBuffer(ParticleTypes.CAMPFIRE_COSY_SMOKE, this.getX(), this.getY(), this.getZ(), 0, 0, 0));
            }
        }
    }

    @Override
    public void tick() {
        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
        if (hitResult.getType() != HitResult.Type.MISS) {
            this.onCollision(hitResult);
        }


        Vec3d adjustedVelocity = ((CollisionsAccessor) this).adjustMovementForCollisionsPublic(this.getVelocity());
        this.setVelocity(adjustedVelocity);

        super.tick();

        if (this.getWorld().getBlockState(this.getBlockPos().add(0,-1,0)).isSolidBlock(this.getWorld(), this.getBlockPos())) {
            this.friction(this.getFrictionAmount());
        }

        this.setFuse(this.getFuse() - 1);

        if (!this.getWorld().isClient) {
            if(this.getFuse() <= 0) {
                this.explode();
            }

            this.spawnGrenadeParticles();
        }
    }
}
