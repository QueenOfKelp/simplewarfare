package queenofkelp.simplewarfare.util;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import queenofkelp.simplewarfare.bullet.entity.BulletEntity;
import queenofkelp.simplewarfare.networking.QPackets;

import java.util.Iterator;
import java.util.function.Predicate;

public class BulletUtil {

    public static HitResult getBulletCollision(BulletEntity entity, Predicate<Entity> entityPredicate, Predicate<BlockPos> blockPosPredicate) {
        Vec3d velocity = entity.getVelocity();
        Vec3d position = entity.getPos();
        Vec3d end = position.add(velocity);

        return blockAndEntityRaycast(position, end, entityPredicate, blockPosPredicate, entity);
    }

    public static HitResult blockAndEntityRaycast(Vec3d start, Vec3d end, Predicate<Entity> entityPredicate, Predicate<BlockPos> blockPosPredicate, Entity entity) {

        Vec3d currentPos = new Vec3d(start.getX(), start.getY(), start.getZ());
        World world = entity.getWorld();
        Vec3d direction = start.subtract(end).normalize().multiply(-1);

        Direction facing = Direction.getFacing(direction.getX(), direction.getY(), direction.getZ());

        while(currentPos.distanceTo(start) <= start.distanceTo(end)) {

            Box offsetBox = entity.getBoundingBox().offset(currentPos.subtract(start));

            Iterator<Entity> iterator = world.getOtherEntities(entity, offsetBox, entityPredicate).iterator();
            BlockPos blockpos = BlockPos.ofFloored(currentPos);
            BlockState blockState = world.getBlockState(blockpos);

            if (iterator.hasNext()) {
                Entity hitEntity = iterator.next();

                return new EntityHitResult(hitEntity, currentPos);
            }
            VoxelShape blockShape = RaycastContext.ShapeType.COLLIDER.get(blockState, world, blockpos, ShapeContext.of(entity));

            boolean shapeIntersectsEntity = false;
            for (Box box : blockShape.getBoundingBoxes()) {
                if (blockShape.isEmpty() || blockState.isAir()) {
                    break;
                }
                Box offsetBlockBox = box.offset(blockpos);
                if (offsetBlockBox.intersects(offsetBox)) {
                    shapeIntersectsEntity = true;
                    break;
                }
            }

            if (blockPosPredicate.test(blockpos) && shapeIntersectsEntity) {
                return new BlockHitResult(currentPos, facing, blockpos, true);
            }

            currentPos = currentPos.add(direction.multiply(.01));
        }
        return BlockHitResult.createMissed(end, facing, BlockPos.ofFloored(end));
    }
}
