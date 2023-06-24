package queenofkelp.simplewarfare.grenade.item;

import net.minecraft.entity.EntityType;
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

public class GrenadeItem extends Item {

    public GrenadeItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand); // creates a new ItemStack instance of the user's itemStack in-hand
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 1F); // plays a globalSoundEvent

        user.getItemCooldownManager().set(this, 100);

        if (!world.isClient) {
            FragGrenadeEntity grenadeEntity = new FragGrenadeEntity(QEntities.FRAG_ENTITY, user, world, 100,
                    .05, false, .1, true);
            grenadeEntity.setItem(itemStack);
            float speed = user.isSneaking() ? .75f : 1.5f;
            grenadeEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, speed, 0F);
                        /*
                        snowballEntity.setProperties(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 1.0F);
                        In 1.17,we will use setProperties instead of setVelocity.
                        */
            world.spawnEntity(grenadeEntity); // spawns entity
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!user.getAbilities().creativeMode) {
            itemStack.decrement(1); // decrements itemStack if user is not in creative mode
        }

        return TypedActionResult.success(itemStack, world.isClient());
    }
}
