package queenofkelp.simplewarfare.util.damage_dropoff;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ThresholdDamageDropoff extends DamageDropoff {

    //public ArrayList<Double> dropoffDistanceThresholds;
    //public ArrayList<Float> damageMultipliers;
    public LinkedHashMap<Double, Float> thresholdMap;

    public float getDamageForDistance(double distance, float damage) {

        float damageMult = (float) this.thresholdMap.values().toArray()[0];

        for (double distanceThreshold : this.thresholdMap.keySet()) {
            if (distance <= distanceThreshold) {
                damageMult = this.thresholdMap.get(distanceThreshold);
            }
        }

        return damageMult * damage;
    }

    /*
    distance thresholds should be organized greatest to least distances!
    Additionally, the index of each distance threshold corresponds with the damage multiplier with that index
    so if the distance threshold 30 is in index 2 and the damage mult 0.6 is also in index 2 then a bullet traveling
    30 blocks or fewer will have the 0.6 mult unless a smaller distance threshold overrides it.
     */
    public ThresholdDamageDropoff(LinkedHashMap<Double, Float> thresholdMap) {
        this.thresholdMap = thresholdMap;
    }

    public ThresholdDamageDropoff add(double distance, float damageMult) {
        this.thresholdMap.put(distance, damageMult);
        return this;
    }

    @Override
    public String getDisplayInformation() {
        StringBuilder thresholdInfo = new StringBuilder("Type: Threshold / ");

        for (double distance : this.thresholdMap.keySet()) {
            thresholdInfo.append(distance).append(" blocks: ").append(this.thresholdMap.get(distance)).append("x damage / ");
        }

        return thresholdInfo.toString();
    }
}
