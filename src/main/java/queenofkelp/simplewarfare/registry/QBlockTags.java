package queenofkelp.simplewarfare.registry;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import queenofkelp.simplewarfare.SimpleWarfare;

public class QBlockTags {
    public static final TagKey<Block> BULLET_PASSABLE = TagKey.of(RegistryKeys.BLOCK, SimpleWarfare.getIdentifier("bullet_passables"));
    public static final TagKey<Block> BULLET_STOPPING = TagKey.of(RegistryKeys.BLOCK, SimpleWarfare.getIdentifier("bullet_stopping"));
    public static final TagKey<Block> BULLET_BREAKABLE = TagKey.of(RegistryKeys.BLOCK, SimpleWarfare.getIdentifier("bullet_breakables"));

}
