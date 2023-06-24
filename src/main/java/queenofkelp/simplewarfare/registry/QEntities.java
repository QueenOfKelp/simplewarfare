package queenofkelp.simplewarfare.registry;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import queenofkelp.simplewarfare.SimpleWarfare;
import queenofkelp.simplewarfare.bullet.entity.BulletEntity;

public class QEntities {

    public static final EntityType<BulletEntity> BULLET_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            SimpleWarfare.getIdentifier("bullet_entity"),
            FabricEntityTypeBuilder.<BulletEntity>create(SpawnGroup.MISC, BulletEntity::new).
                    dimensions(EntityDimensions.fixed(0.1F, 0.1F)).trackRangeBlocks(30).
                    trackedUpdateRate(1).build()
    );

    public static void registerClient() {
        EntityRendererRegistry.register(BULLET_ENTITY, FlyingItemEntityRenderer::new);
    }

}
