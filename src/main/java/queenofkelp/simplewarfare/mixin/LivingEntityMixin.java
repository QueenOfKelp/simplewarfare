package queenofkelp.simplewarfare.mixin;

import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
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

        if (!source.isIn(QTags.USES_BULLETPROOF_PROECTION_INSTEAD)) {
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

    @Inject(method = "applyArmorToDamage", at = @At("HEAD"), cancellable = true)
    protected void applyArmorToDamage(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        if (source.isIn(QTags.USES_BULLETPROOF_PROECTION_INSTEAD)) {
            cir.setReturnValue(amount);
        }
    }
}