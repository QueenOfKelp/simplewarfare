package queenofkelp.simplewarfare.bullet.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import queenofkelp.simplewarfare.bullet.entity.BulletEntity;
import queenofkelp.simplewarfare.util.damage_dropoff.DamageDropoff;

public class BulletItem extends Item {

    public AmmoType type;

    public BulletItem(Settings settings, AmmoType type) {
        super(settings);
        this.type = type;
    }

    public AmmoType getBulletType() {
        return this.type;
    }

    public BulletEntity createBulletForItem(LivingEntity shooter, World world, float damage, int fireRate, int penetration, DamageDropoff dropOff, double penetrationDropoff) {
        return new BulletEntity(shooter, world, damage, fireRate, penetration, dropOff, penetrationDropoff);
    }

}
