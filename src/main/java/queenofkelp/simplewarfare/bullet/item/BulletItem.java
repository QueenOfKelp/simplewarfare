package queenofkelp.simplewarfare.bullet.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import queenofkelp.simplewarfare.bullet.entity.BulletEntity;
import queenofkelp.simplewarfare.util.damage_dropoff.DamageDropoff;

import java.util.List;

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

        /*
        BulletEntity bullet = new BulletEntity(QEntities.BULLET_ENTITY, shooter, world, damage, fireRate, penetration, dropOff, penetrationDropoff);
//a
        bullet.setPosition(pos);
        bullet.setVelocity(shooter, pitch, yaw, 0.0F, velocity, bloom);

        return bullet;
         */
        return null;
    }

    @Override
    public void appendTooltip(ItemStack bullet, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal(
                        "Ammo Type: " + this.getBulletType().getDisplayName().getString())
                .formatted(Formatting.RESET).fillStyle(Style.EMPTY.withColor(Formatting.GOLD)));
    }
}
