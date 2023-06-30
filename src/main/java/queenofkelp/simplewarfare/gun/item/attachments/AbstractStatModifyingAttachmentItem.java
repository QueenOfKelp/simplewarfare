package queenofkelp.simplewarfare.gun.item.attachments;

import queenofkelp.simplewarfare.util.damage_dropoff.DamageDropoff;
import queenofkelp.simplewarfare.util.gun.GunBloom;
import queenofkelp.simplewarfare.util.gun.GunSound;

public abstract class AbstractStatModifyingAttachmentItem extends GunAttachmentItem{
    public AbstractStatModifyingAttachmentItem(Settings settings) {
        super(settings);
    }

    public float modifyDamage(float damage) {
        return damage;
    }
    public int modifyFireRate(int fireRate) {
        return fireRate;
    }
    public int modifyEquipTime(int time) {
        return time;
    }
    public float modifyVelocity(float velocity) {
        return velocity;
    }
    public float modifyRecoil(float recoil) {
        return recoil;
    }
    public GunBloom modifyBloom(GunBloom bloom) {
        return bloom;
    }
    public float modifyAdsFovMult(float mult) {
        return mult;
    }
    public float modifySpeed(float speed) {
        return speed;
    }
    public float modifyAdsSpeed(float speed) {
        return speed;
    }
    public int modifyPenetration(int penetration) {
        return penetration;
    }
    public double modifyMaxPenetrationDropoff(double maxPenetrationDropoff) {
        return maxPenetrationDropoff;
    }
    public DamageDropoff modifyDamageDropoff(DamageDropoff dropoff) {
        return dropoff;
    }
    public GunSound modifyGunSound(GunSound sound) {
        return sound;
    }
    public boolean modifyIsAutomatic(boolean isAutomatic) {
        return isAutomatic;
    }
    public int modifyReloadTime(int time) {
        return time;
    }
    public int modifyMaxAmmo(int maxAmmo) {
        return maxAmmo;
    }
}
