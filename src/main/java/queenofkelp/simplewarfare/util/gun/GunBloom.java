package queenofkelp.simplewarfare.util.gun;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class GunBloom {
    //Bloom means the amount of randomness in a bullet's trajectory

    public float bloomDegrees;
    public float movementBloomMult;
    public float maxMovementBloomMult;
    public float ADSBloomMult;
    public boolean canADS;
    public float crouchingBloomMult;
    public float crawlingBloomMult;

    public GunBloom(float bloomDegrees, float movementBloomMult, float maxMovementBloomMult,
                    float ADSBloomMult, boolean canADS, float crouchingBloomMult, float crawlingBloomMult) {
        this.bloomDegrees = bloomDegrees;
        this.movementBloomMult = movementBloomMult;
        this.maxMovementBloomMult = maxMovementBloomMult;
        this.ADSBloomMult = ADSBloomMult;
        this.canADS = canADS;
        if (!canADS) {
            this.ADSBloomMult = 1;
        }
        this.crouchingBloomMult = crouchingBloomMult;
        this.crawlingBloomMult = crawlingBloomMult;
    }

    public float getTotalBloom(PlayerEntity shooter) {
        float bloom = this.bloomDegrees;

        bloom = bloom * this.getMovementInaccuracyMult(shooter);

        bloom = (shooter.isSneaking() && !shooter.isCrawling()) ? (bloom * this.crouchingBloomMult) : bloom;
        bloom = (shooter.isCrawling()) ? (bloom * this.crawlingBloomMult) : bloom;
        bloom = (GunShooterUtil.isPlayerADSing(shooter)) ? (bloom * this.ADSBloomMult) : bloom;

        return bloom;
    }

    public float getMovementInaccuracyMult(PlayerEntity shooter) {
        Vec3d previousPos = GunShooterUtil.getPlayerPreviousPosition(shooter);
        Vec3d currentPos = shooter.getPos();

        float movementInaccuracyMult = (float) Math.min(currentPos.distanceTo(previousPos) * this.movementBloomMult, this.maxMovementBloomMult);

        return Math.max(movementInaccuracyMult, 1);
    }
}
