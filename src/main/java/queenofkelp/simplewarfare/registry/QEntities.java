package queenofkelp.simplewarfare.registry;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import queenofkelp.simplewarfare.SimpleWarfare;
import queenofkelp.simplewarfare.bullet.entity.BulletEntity;
import queenofkelp.simplewarfare.bullet.entity.BulletEntityModel;
import queenofkelp.simplewarfare.bullet.entity.BulletEntityRenderer;

public class QEntities {

    public static final EntityType<BulletEntity> BULLET_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            SimpleWarfare.getIdentifier("bullet_entity"),
            FabricEntityTypeBuilder.<BulletEntity>create(SpawnGroup.MISC, BulletEntity::new).
                    dimensions(EntityDimensions.fixed(0.1F, 0.1F)).trackRangeBlocks(30).
                    trackedUpdateRate(1).build()
    );
    public static final EntityModelLayer MODEL_BULLET_TRACER = new EntityModelLayer(new Identifier(SimpleWarfare.MOD_ID, "bullet_tracer"), "");

    public static void registerClient() {
        EntityRendererRegistry.register(BULLET_ENTITY, BulletEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_BULLET_TRACER, BulletEntityModel::getTexturedModelData);
    }

}
