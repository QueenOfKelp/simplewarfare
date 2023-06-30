package queenofkelp.simplewarfare.bulletproof_armor;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;

public interface BulletproofArmorMaterial extends ArmorMaterial {
    int getDurability(ArmorItem.Type type);

    int getProtection(ArmorItem.Type type);

    int getBulletproofProtection(ArmorItem.Type type);

    int getEnchantability();

    SoundEvent getEquipSound();

    Ingredient getRepairIngredient();

    String getName();

    float getToughness();

    float getKnockbackResistance();
}
