package queenofkelp.simplewarfare.bullet.item;

import net.minecraft.text.Text;

public enum AmmoType {
    MEDIUM(Text.literal("5.56")),
    HEAVY(Text.literal(".50"));


    public final Text displayName;
    AmmoType(Text displayName) {
        this.displayName = displayName;
    }
}
