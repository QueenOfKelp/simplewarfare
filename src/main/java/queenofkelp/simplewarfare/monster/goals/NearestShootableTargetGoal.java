package queenofkelp.simplewarfare.monster.goals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.function.Predicate;

public class NearestShootableTargetGoal<T extends LivingEntity> extends TrackTargetGoal {

    protected final Class<T> targetClass;

    protected final double rangeX;
    protected final double rangeY;
    @Nullable
    protected LivingEntity target;
    protected TargetPredicate targetPredicate;

    public NearestShootableTargetGoal(MobEntity mob, Class<T> targetClass, boolean checkVisibility, boolean checkNavigable, double rangeX, double rangeY, @Nullable Predicate<LivingEntity> targetPredicate) {
        super(mob, checkVisibility, checkNavigable);
        this.targetClass = targetClass;
        this.setControls(EnumSet.of(Control.TARGET));
        this.targetPredicate = checkVisibility? TargetPredicate.createAttackable().setBaseMaxDistance(this.getFollowRange()).setPredicate(targetPredicate) : TargetPredicate.createAttackable().setBaseMaxDistance(this.getFollowRange()).setPredicate(targetPredicate).ignoreVisibility();
        this.rangeX = rangeX;
        this.rangeY = rangeY;
    }

    @Override
    public boolean canStart() {
        this.findTarget();
        return this.target != null;
    }

    protected Box getTargetSearchArea() {
        return this.mob.getBoundingBox().expand(this.rangeX, this.rangeY, this.rangeX);
    }

    protected void findTarget() {
        if (this.targetClass != PlayerEntity.class && this.targetClass != ServerPlayerEntity.class) {
            this.target = this.mob.world.getClosestEntity(this.mob.world.getEntitiesByClass(this.targetClass, this.getTargetSearchArea(), (entityliving) -> {
                return true;
            }), this.targetPredicate, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
        } else {
            this.target = this.mob.world.getClosestPlayer(this.targetPredicate, this.mob, this.rangeX, this.rangeY, this.rangeX);
        }

    }

    public void start() {
        this.mob.setTarget(this.target);
        super.start();
    }

    public void setTarget(@Nullable LivingEntity entityliving) {
        this.target = entityliving;
    }


}
