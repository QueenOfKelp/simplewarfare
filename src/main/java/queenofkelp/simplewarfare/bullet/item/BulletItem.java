package queenofkelp.simplewarfare.bullet.item;

import net.minecraft.item.Item;

public class BulletItem extends Item {

    public AmmoType type;

    public BulletItem(Settings settings, AmmoType type) {
        super(settings);
        this.type = type;
    }

    public AmmoType getBulletType() {
        return this.type;
    }
}
