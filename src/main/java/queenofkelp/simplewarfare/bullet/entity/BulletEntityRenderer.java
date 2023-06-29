package queenofkelp.simplewarfare.bullet.entity;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import queenofkelp.simplewarfare.SimpleWarfare;
import queenofkelp.simplewarfare.registry.QEntities;

public class BulletEntityRenderer extends EntityRenderer<BulletEntity> {
    public BulletEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(BulletEntity entity) {
        return new Identifier(SimpleWarfare.MOD_ID, "textures/item/ak47");
    }
}
