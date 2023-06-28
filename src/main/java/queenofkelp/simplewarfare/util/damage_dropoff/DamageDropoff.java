package queenofkelp.simplewarfare.util.damage_dropoff;

public abstract class DamageDropoff {

    public float getDamageForDistance(double distance, float damage) {
        return 1;
    }

    public String getDisplayInformation() {
        return "no info to display";
    }
}
