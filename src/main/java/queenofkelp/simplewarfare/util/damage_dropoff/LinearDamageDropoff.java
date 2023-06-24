package queenofkelp.simplewarfare.util.damage_dropoff;

public class LinearDamageDropoff extends DamageDropoff{

    public float dropoffAmount;
    public float interval;
    public float minimumDamage;

    public LinearDamageDropoff(float dropoffAmount, float interval, float minimumDamage) {
        this.dropoffAmount = dropoffAmount;
        this.interval = interval;
        this.minimumDamage = minimumDamage;
    }

    public float getDamageForDistance(double distance, float damage) {
        damage = damage - this.dropoffAmount * ((int) (distance / this.interval));
        return Math.max(damage, this.minimumDamage);
    }
}
