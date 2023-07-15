package queenofkelp.simplewarfare.bullet.item;

import net.minecraft.text.Text;

public enum AmmoType implements IAmmoType{
    MEDIUM(Text.literal("5.56")),
    HEAVY(Text.literal(".50"));


    public final Text displayName;
    AmmoType(Text displayName) {
        this.displayName = displayName;
    }

    public Text getDisplayName() {
        return displayName;
    }
}
