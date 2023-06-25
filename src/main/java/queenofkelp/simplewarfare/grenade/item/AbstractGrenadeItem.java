package queenofkelp.simplewarfare.grenade.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import queenofkelp.simplewarfare.grenade.entity.AbstractGrenadeEntity;
import queenofkelp.simplewarfare.grenade.entity.FragGrenadeEntity;
import queenofkelp.simplewarfare.registry.QEntities;

public abstract class AbstractGrenadeItem extends Item {

    public AbstractGrenadeItem(Settings settings) {
        super(settings);
    }

    public AbstractGrenadeEntity createDefaultGrenade(LivingEntity owner, World world) {
        return null;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 1F);

        user.getItemCooldownManager().set(this, 100);

        AbstractGrenadeEntity grenadeEntity = this.createDefaultGrenade(user, world);

        float speed = user.isSneaking() ? .75f : 1.5f;
        grenadeEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, speed, 0F);

        world.spawnEntity(grenadeEntity);

        user.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!user.getAbilities().creativeMode) {
            itemStack.decrement(1);
        }

        return TypedActionResult.success(itemStack, world.isClient());
    }
}
