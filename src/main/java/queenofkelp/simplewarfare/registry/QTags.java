package queenofkelp.simplewarfare.registry;

import net.minecraft.block.Block;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import queenofkelp.simplewarfare.SimpleWarfare;

public class QTags {
    public static final TagKey<Block> BULLET_PASSABLE = TagKey.of(RegistryKeys.BLOCK, SimpleWarfare.getIdentifier("bullet_passables"));
    public static final TagKey<Block> BULLET_STOPPING = TagKey.of(RegistryKeys.BLOCK, SimpleWarfare.getIdentifier("bullet_stopping"));
    public static final TagKey<Block> BULLET_BREAKABLE = TagKey.of(RegistryKeys.BLOCK, SimpleWarfare.getIdentifier("bullet_breakables"));

    public static final TagKey<DamageType> USES_BULLETPROOF_PROECTION_INSTEAD = TagKey.of(RegistryKeys.DAMAGE_TYPE, SimpleWarfare.getIdentifier("uses_bullet_protection_not_armor"));
}
