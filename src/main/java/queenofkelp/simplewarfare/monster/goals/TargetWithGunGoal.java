package queenofkelp.simplewarfare.monster.goals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;

public class TargetWithGunGoal extends Goal {
    protected final MobEntity mob;
    private final double speedModifier;
    private final double maxDistance;
    private final boolean checkVisibility;

    public TargetWithGunGoal(MobEntity mob, double speedModifier, double maxDistance) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.maxDistance = maxDistance;
        this.checkVisibility = false;
    }

    public TargetWithGunGoal(MobEntity mob, double speedModifier, double maxDistance, boolean checkVisibility) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.maxDistance = maxDistance;
        this.checkVisibility = checkVisibility;
    }

    @Override
    public boolean canStart() {
        return true;
    }

    public void start() {
        this.mob.setAttacking(true);
    }

    public void stop() {
        this.mob.setAttacking(false);
        this.mob.getNavigation().stop();
    }

    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target != null) {
            this.mob.getLookControl().lookAt(target, 360.0F, 360.0F);
            double distance = this.mob.distanceTo(target);

            if (distance > this.maxDistance) {

                this.mob.getNavigation().startMovingTo(target, 1);
            }
            else if (this.mob.canSee(target) || !this.checkVisibility) {
                this.mob.getNavigation().startMovingTo(target, this.speedModifier);
            }
            else {
                this.mob.getNavigation().startMovingTo(target, 1);
            }
        }
    }
}
