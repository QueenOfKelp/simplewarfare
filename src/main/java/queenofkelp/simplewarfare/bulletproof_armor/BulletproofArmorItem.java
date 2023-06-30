package queenofkelp.simplewarfare.bulletproof_armor;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class BulletproofArmorItem extends ArmorItem {

    public BulletproofArmorItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        int bulletproofProtection = (this.getMaterial() instanceof BulletproofArmorMaterial m ? m.getBulletproofProtection(this.type) : 0);
        if (bulletproofProtection > 0) {
            tooltip.add(1, Text.literal(
                            "+" + bulletproofProtection + " Bulletproof Armor")
                    .formatted(Formatting.RESET).formatted(Formatting.BLUE));
        }
    }
}
