package queenofkelp.simplewarfare.util.damage_dropoff;

public class ExponentialDamageDropoff extends DamageDropoff{

    public float interval;
    public float minimumDamage;
    public float dropoffMultiplier;

    public ExponentialDamageDropoff(float dropoffMultiplier, float interval, float minimumDamage) {
        this.dropoffMultiplier = dropoffMultiplier;
        this.interval = interval;
        this.minimumDamage = minimumDamage;
    }

    public float getDamageForDistance(double distance, float damage) {
        damage = damage * (float) Math.pow(this.dropoffMultiplier, ((int) (distance / this.interval)));
        return Math.max(damage, this.minimumDamage);
    }

}
