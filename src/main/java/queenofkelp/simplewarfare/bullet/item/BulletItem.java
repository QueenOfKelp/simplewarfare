package queenofkelp.simplewarfare.bullet.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import queenofkelp.simplewarfare.bullet.entity.BulletEntity;
import queenofkelp.simplewarfare.util.damage_dropoff.DamageDropoff;

public class BulletItem extends Item {

    public IAmmoType type;

    public BulletItem(Settings settings, IAmmoType type) {
        super(settings);
        this.type = type;
    }

    public IAmmoType getBulletType() {
        return this.type;
    }

    public BulletEntity createBulletForItem(LivingEntity shooter, World world, float damage, int fireRate, int penetration, DamageDropoff dropOff, double penetrationDropoff,
                                            Vec3d pos, float bloom, float velocity, float pitch, float yaw) {
        BulletEntity bullet = new BulletEntity(shooter, world, damage, fireRate, penetration, dropOff, penetrationDropoff);

        bullet.setPosition(pos);
        bullet.setVelocity(shooter, pitch, yaw, 0.0F, velocity, bloom);

        return bullet;
    }

}
