package queenofkelp.simplewarfare.util.damage_dropoff;

import java.util.ArrayList;
import java.util.HashMap;

public class ThresholdDamageDropoff extends DamageDropoff {

    public ArrayList<Double> dropoffDistanceThresholds;
    public ArrayList<Float> damageMultipliers;

    public float getDamageForDistance(double distance, float damage) {

        float damageMult = this.damageMultipliers.get(0);

        for (double distanceThreshold : this.dropoffDistanceThresholds) {
            if (distance <= distanceThreshold) {
                damageMult = this.damageMultipliers.get(this.dropoffDistanceThresholds.indexOf(distanceThreshold));
            }
        }

        return damageMult * damage;
    }

    /*
    distance thresholds should be organized greatest to least distances!
    Additionally, the index of each distance threshold corresponds with the damage multiplier with that index
    so if the distance threshold 30 is in index 2 and the damage mult 0.6 is also in index 2 then an bullet traveling
    30 blocks or less will have the 0.6 mult unless a smaller distance threshold overrides it.
     */
    public ThresholdDamageDropoff(ArrayList<Double> dropoffDistanceThresholds, ArrayList<Float> damageMultipliers) {
        this.dropoffDistanceThresholds = dropoffDistanceThresholds;
        this.damageMultipliers = damageMultipliers;
    }

}
