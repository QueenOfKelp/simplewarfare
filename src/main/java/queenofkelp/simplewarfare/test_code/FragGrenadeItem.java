package queenofkelp.simplewarfare.test_code;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import queenofkelp.simplewarfare.grenade.entity.AbstractGrenadeEntity;
import queenofkelp.simplewarfare.grenade.item.AbstractGrenadeItem;
import queenofkelp.simplewarfare.registry.QEntities;

public class FragGrenadeItem extends AbstractGrenadeItem {
    public FragGrenadeItem(Settings settings) {
        super(settings);
    }

    @Override
    public AbstractGrenadeEntity createDefaultGrenade(LivingEntity owner, World world) {
        return new FragGrenadeEntity(QEntities.FRAG_ENTITY, owner, world, 100, .05, false, .1);
    }
}
