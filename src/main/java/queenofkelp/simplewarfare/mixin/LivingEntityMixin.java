package queenofkelp.simplewarfare.mixin;

import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import queenofkelp.simplewarfare.bulletproof_armor.BulletproofArmorMaterial;
import queenofkelp.simplewarfare.registry.QTags;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow public abstract Iterable<ItemStack> getArmorItems();

    public float applyBulletproofArmorToDamage(DamageSource source, float amount) {
        int bulletproofArmor = 0;

        if (!source.isIn(QTags.BULLETPROOF_ARMOR_STOPS)) {
            return amount;
        }

        for (ItemStack armorItemStack : this.getArmorItems()) {
            if (armorItemStack.getItem() instanceof ArmorItem armorItem) {
                if (armorItem.getMaterial() instanceof BulletproofArmorMaterial bulletproofArmorMaterial) {
                    bulletproofArmor = bulletproofArmor + bulletproofArmorMaterial.getBulletproofProtection(armorItem.getType());
                }
            }
        }

        amount = DamageUtil.getDamageLeft(amount, bulletproofArmor, 0);

        return amount;
    }

    @ModifyVariable(method = "applyDamage", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float injectedNewDamageAmount(float original, DamageSource source, float amount) {
        return applyBulletproofArmorToDamage(source, original);
    }
}