package queenofkelp.simplewarfare.bullet.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import queenofkelp.simplewarfare.SimpleWarfare;
import queenofkelp.simplewarfare.registry.QTags;
import queenofkelp.simplewarfare.util.BulletUtil;
import queenofkelp.simplewarfare.util.damage_dropoff.DamageDropoff;

import java.util.ArrayList;
import java.util.HashMap;

public class BulletEntity extends ThrownItemEntity {
    protected double bounceAmount = .5;
    protected float damage;
    protected int collisions = 0;
    protected int fireRate;
    protected int penetration;
    protected double penetrationMaxDropOff = 0;
    protected double blocksTraveled = 0;
    protected ArrayList<Entity> hitEntities = new ArrayList<>();
    protected ArrayList<BlockPos> hitBlockPoss = new ArrayList<>();
    protected HashMap<Entity, Integer> unhittableEntities = new HashMap<>();
    protected HashMap<BlockPos, Integer> unhittableBlockPoss = new HashMap<>();
    protected boolean wallBanging = false;
    protected DamageDropoff damageDropOff;

    public BulletEntity(EntityType<? extends BulletEntity> entityType, World world) {
        super(entityType, world);
    }
//a
    public BulletEntity(EntityType<? extends BulletEntity> entityType, LivingEntity owner, World world) {
        super(entityType, owner, world);
    }

    public BulletEntity(EntityType<? extends BulletEntity> entityType, LivingEntity owner, World world, float damage, int fireRate,
                        int penetration, DamageDropoff damageDropOff, double penetrationMaxDropOff) {
        super(entityType, owner, world);
        this.damage = damage;
        this.fireRate = fireRate;
        this.penetration = penetration;
        this.penetrationMaxDropOff = penetrationMaxDropOff;
        this.damageDropOff = damageDropOff;
    }

    public BulletEntity(EntityType<? extends BulletEntity> entityType, double x, double y, double z, World world) {
        super(entityType, x, y, z, world);
    }

    protected double getBounceAmount() {
        return this.bounceAmount;
    }


    @Override
    protected Item getDefaultItem() {
        return null;
    }

    protected void onEntityHit(EntityHitResult entityHitResult) {

        if (this.getWorld().isClient) {
            return;
        }

        if (this.damageDropOff == null) {
            this.discard();
            return;
        }

        Entity entity = entityHitResult.getEntity();
        this.hitEntities.add(entity);

        if (unhittableEntities.get(entity) != null && unhittableEntities.get(entity) > ticksExisted) {
            return;
        }

        unhittableEntities.put(entity, ticksExisted + 4);

        super.onEntityHit(entityHitResult);

        float finalDamage = damage;
        finalDamage = this.damageDropOff.getDamageForDistance(blocksTraveled, finalDamage);
        finalDamage = finalDamage * (float) Math.pow(this.penetrationMaxDropOff, collisions/(float) this.penetration); //damage reduction for penetration

        dealBulletDamage(entity, finalDamage);
        doBulletEffects(entity);

        this.collisions++;

        if (this.collisions > this.penetration) {
            this.discard();
        }

    }

    protected void dealBulletDamage(Entity victim, float damage) {
        victim.damage(this.getDamageSources().create(RegistryKey.of(RegistryKeys.DAMAGE_TYPE, SimpleWarfare.getIdentifier("bullet")), (this.getOwner() instanceof LivingEntity livingOwner ? livingOwner : null)), damage);
    }

    protected void doBulletEffects(Entity victim) {
        if (victim instanceof LivingEntity livingVictim) {
            livingVictim.timeUntilRegen = Math.min(this.fireRate, 10);
        }
    }

    protected boolean canHit(Entity entity) {
        return super.canHit(entity) && (this.hitEntities == null || !this.hitEntities.contains(entity));
    }

    protected boolean canHit(BlockPos blockpos) {
        return (this.hitBlockPoss == null || !this.hitBlockPoss.contains(blockpos));
    }

    protected void onCollision(HitResult hitResult) {
        if (dontCollide) {
            return;
        }
        super.onCollision(hitResult);
        if (!this.world.isClient) {

            if (hitResult.getType() == HitResult.Type.BLOCK) {
                Vec3d oldVelocity = this.getVelocity();
                BlockHitResult blockHitResult = (BlockHitResult) hitResult;

                this.hitBlockPoss.add(blockHitResult.getBlockPos());

                if (unhittableBlockPoss.get(blockHitResult.getBlockPos()) != null && unhittableBlockPoss.get(blockHitResult.getBlockPos()) > ticksExisted) {
                    return;
                }

                unhittableBlockPoss.put(blockHitResult.getBlockPos(), ticksExisted + 20);

                this.collisions++;

                BlockState block = this.getWorld().getBlockState(blockHitResult.getBlockPos());

                if (!block.isIn(QTags.BULLET_PASSABLE)) {
                    this.wallBanging = false;
                }

                if (block.isIn(QTags.BULLET_BREAKABLE)) {
                    this.getWorld().breakBlock(blockHitResult.getBlockPos(), false, this.getOwner());
                }
                else if (block.isIn(QTags.BULLET_STOPPING)) {
                    this.discard();
                }
                else if (block.isIn(QTags.BULLET_PASSABLE)) {
                    this.wallBanging = true;
                }
                else {
                    world.playSound(null, this.getBlockPos(), SoundEvents.BLOCK_BELL_USE, SoundCategory.MASTER, 1f, 2f);
                    this.bounce(this.getBounceAmount(), blockHitResult.getSide(), oldVelocity);
                }
                if (collisions > penetration) {
                    this.discard();
                }
            }
        }
    }

    protected void bounce(double amount, Direction direction, Vec3d startingVelocity) {
        if (direction.equals(Direction.NORTH) || direction.equals(Direction.SOUTH)) {
            this.setPitch(this.getPitch()*-1);
            this.setVelocity(startingVelocity.getX(), startingVelocity.getY(), startingVelocity.getZ() * (-amount));
        }
        else if (direction.equals(Direction.EAST) || direction.equals(Direction.WEST)) {
            this.setPitch(this.getPitch()*-1);
            this.setVelocity(startingVelocity.getX() * (-amount), startingVelocity.getY(), startingVelocity.getZ());
        }
        else if (direction.equals(Direction.UP) || direction.equals(Direction.DOWN)) {
            this.setYaw(this.getYaw()*-1);
            this.setVelocity(startingVelocity.getX(), startingVelocity.getY() * (-amount), startingVelocity.getZ());
        }
    }

    Vec3d simulatedPos = null;
    boolean dontCollide = false;
    int ticksExisted = 0;

    @Override
    public void tick() {

        this.dontCollide = false;

        ticksExisted++;

        while(!this.isRemoved()) {

            if (this.getWorld().isClient) {
                break;
            }

            HitResult hitResult = BulletUtil.getBulletCollision(this, this::canHit, this::canHit);

            Vec3d posToUse = (simulatedPos == null) ? this.getPos() : simulatedPos;

            if (hitResult.getType().equals(HitResult.Type.ENTITY)) {
                simulatedPos = hitResult.getPos();
                this.blocksTraveled = this.blocksTraveled + posToUse.distanceTo(((EntityHitResult) hitResult).getEntity().getPos());

                this.onCollision(hitResult);
            }
            else if (hitResult.getType().equals(HitResult.Type.BLOCK)){
                simulatedPos = hitResult.getPos();
                this.blocksTraveled = this.blocksTraveled + posToUse.distanceTo((hitResult).getPos());

                this.onCollision(hitResult);
            }
            else {
                this.simulatedPos = null;
                this.blocksTraveled = this.blocksTraveled + posToUse.distanceTo(this.getPos().add(this.getVelocity()));
                break;
            }
        }

        this.hitEntities = new ArrayList<>();
        this.hitBlockPoss = new ArrayList<>();

        //TODO sound effect while flying near player
        this.getWorld().addParticle(ParticleTypes.CRIT, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);

        this.dontCollide = true;
        super.tick();
    }


}
