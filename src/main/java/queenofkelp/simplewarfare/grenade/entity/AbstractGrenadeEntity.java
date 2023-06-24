package queenofkelp.simplewarfare.grenade.entity;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
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
import java.util.List;

public abstract class AbstractGrenadeEntity extends ThrownItemEntity {

    protected int startFuse;
    protected int fuse;

    protected double frictionAmount;

    protected boolean explodeOnImpact;

    protected double bounceAmount;

    protected boolean spawnParticles;

    public AbstractGrenadeEntity(EntityType<? extends AbstractGrenadeEntity> entityType, World world) {
        super(entityType, world);
    }

    public AbstractGrenadeEntity(EntityType<? extends AbstractGrenadeEntity> entityType, LivingEntity owner, World world,
                                 int startFuse, double frictionAmount, boolean explodeOnImpact, double bounceAmount, boolean spawnParticles) {
        super(entityType, owner, world);

        this.startFuse = startFuse;
        this.fuse = startFuse;
        this.frictionAmount = frictionAmount;
        this.explodeOnImpact = explodeOnImpact;
        this.bounceAmount = bounceAmount;
        this.spawnParticles  = spawnParticles;
    }

    public AbstractGrenadeEntity(EntityType<? extends AbstractGrenadeEntity> entityType, double x, double y, double z, World world) {
        super(entityType, x, y, z, world);
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
    protected Item getDefaultItem() {
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
        this.kill();
    }

    @Override
    public void tick() {
        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
        if (hitResult.getType() != HitResult.Type.MISS) {
            this.onCollision(hitResult);
        }

        Vec3d adjustedVelocity = adjustMovementForCollisions(this.getVelocity());
        this.setVelocity(adjustedVelocity);

        super.tick();

        if (this.getWorld().getBlockState(this.getBlockPos().add(0,-1,0)).isSolidBlock(this.getWorld(), this.getBlockPos())) {
            this.friction(this.getFrictionAmount());
        }

        this.setFuse(this.getFuse() - 1);
        if (this.spawnParticles) {
            this.getWorld().addParticle(ParticleTypes.SMOKE, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }
        if(this.getFuse() <= 0) {
            this.explode();
        }

    }

    private Vec3d adjustMovementForCollisions(Vec3d movement) {
        Box box = this.getBoundingBox();
        List<VoxelShape> list = this.world.getEntityCollisions(this, box.stretch(movement));
        Vec3d vec3d = movement.lengthSquared() == 0.0 ? movement : adjustMovementForCollisions(this, movement, box, this.world, list);
        boolean bl = movement.x != vec3d.x;
        boolean bl2 = movement.y != vec3d.y;
        boolean bl3 = movement.z != vec3d.z;
        boolean bl4 = this.onGround || bl2 && movement.y < 0.0;
        if (.5 > 0.0F && bl4 && (bl || bl3)) {
            Vec3d vec3d2 = adjustMovementForCollisions(this, new Vec3d(movement.x, (double).5, movement.z), box, this.world, list);
            Vec3d vec3d3 = adjustMovementForCollisions(this, new Vec3d(0.0, (double).5, 0.0), box.stretch(movement.x, 0.0, movement.z), this.world, list);
            if (vec3d3.y < .5) {
                Vec3d vec3d4 = adjustMovementForCollisions(this, new Vec3d(movement.x, 0.0, movement.z), box.offset(vec3d3), this.world, list).add(vec3d3);
                if (vec3d4.horizontalLengthSquared() > vec3d2.horizontalLengthSquared()) {
                    vec3d2 = vec3d4;
                }
            }

            if (vec3d2.horizontalLengthSquared() > vec3d.horizontalLengthSquared()) {
                return vec3d2.add(adjustMovementForCollisions(this, new Vec3d(0.0, -vec3d2.y + movement.y, 0.0), box.offset(vec3d2), this.world, list));
            }
        }

        return vec3d;
    }

    public static Vec3d adjustMovementForCollisions(@Nullable Entity entity, Vec3d movement, Box entityBoundingBox, World world, List<VoxelShape> collisions) {
        ImmutableList.Builder<VoxelShape> builder = ImmutableList.builderWithExpectedSize(collisions.size() + 1);
        if (!collisions.isEmpty()) {
            builder.addAll(collisions);
        }

        WorldBorder worldBorder = world.getWorldBorder();
        boolean bl = entity != null && worldBorder.canCollide(entity, entityBoundingBox.stretch(movement));
        if (bl) {
            builder.add(worldBorder.asVoxelShape());
        }

        builder.addAll(world.getBlockCollisions(entity, entityBoundingBox.stretch(movement)));
        return adjustMovementForCollisions(movement, entityBoundingBox, builder.build());
    }

    private static Vec3d adjustMovementForCollisions(Vec3d movement, Box entityBoundingBox, List<VoxelShape> collisions) {
        if (collisions.isEmpty()) {
            return movement;
        } else {
            double d = movement.x;
            double e = movement.y;
            double f = movement.z;
            if (e != 0.0) {
                e = VoxelShapes.calculateMaxOffset(Direction.Axis.Y, entityBoundingBox, collisions, e);
                if (e != 0.0) {
                    entityBoundingBox = entityBoundingBox.offset(0.0, e, 0.0);
                }
            }

            boolean bl = Math.abs(d) < Math.abs(f);
            if (bl && f != 0.0) {
                f = VoxelShapes.calculateMaxOffset(Direction.Axis.Z, entityBoundingBox, collisions, f);
                if (f != 0.0) {
                    entityBoundingBox = entityBoundingBox.offset(0.0, 0.0, f);
                }
            }

            if (d != 0.0) {
                d = VoxelShapes.calculateMaxOffset(Direction.Axis.X, entityBoundingBox, collisions, d);
                if (!bl && d != 0.0) {
                    entityBoundingBox = entityBoundingBox.offset(d, 0.0, 0.0);
                }
            }

            if (!bl && f != 0.0) {
                f = VoxelShapes.calculateMaxOffset(Direction.Axis.Z, entityBoundingBox, collisions, f);
            }

            return new Vec3d(d, e, f);
        }
    }
}
