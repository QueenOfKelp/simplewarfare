package queenofkelp.simplewarfare.test_code;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import queenofkelp.simplewarfare.grenade.entity.AbstractGrenadeEntity;


public class FragGrenadeEntity extends AbstractGrenadeEntity {


    public FragGrenadeEntity(EntityType<? extends AbstractGrenadeEntity> entityType, World world) {
        super(entityType, world);
    }

    public FragGrenadeEntity(EntityType<? extends AbstractGrenadeEntity> entityType, LivingEntity owner, World world, int startFuse, double frictionAmount, boolean explodeOnImpact, double bounceAmount) {
        super(entityType, owner, world, startFuse, frictionAmount, explodeOnImpact, bounceAmount);
    }

    public FragGrenadeEntity(EntityType<? extends AbstractGrenadeEntity> entityType, double x, double y, double z, World world) {
        super(entityType, x, y, z, world);
    }

    @Override
    protected void explode(){
        super.explode();
        this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), 4, World.ExplosionSourceType.NONE);
    }

    @Override
    protected Item getDefaultItem() {
        return (Item) Items.ACACIA_BOAT; // We will configure this later, once we have created the ProjectileItem.
    }
}