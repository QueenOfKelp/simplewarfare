package queenofkelp.simplewarfare;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import queenofkelp.simplewarfare.bullet.item.AmmoType;
import queenofkelp.simplewarfare.bullet.item.BulletItem;
import queenofkelp.simplewarfare.grenade.item.FragGrenadeItem;
import queenofkelp.simplewarfare.gun.item.Gun;
import queenofkelp.simplewarfare.gun.item.attachments.GunAttachmentItem;
import queenofkelp.simplewarfare.gun.item.attachments.TestStatAttachment;
import queenofkelp.simplewarfare.networking.QPackets;
import queenofkelp.simplewarfare.util.damage_dropoff.ThresholdDamageDropoff;
import queenofkelp.simplewarfare.util.gun.GunBloom;
import queenofkelp.simplewarfare.util.gun.GunSound;

import java.util.LinkedHashMap;

public class SimpleWarfare implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("simplewarfare");
    public static final String MOD_ID = "simplewarfare";

    public static Identifier getIdentifier(String path) {
        return new Identifier(MOD_ID, path);
    }

    public static final Gun AK47 = new Gun(new FabricItemSettings().maxDamage(210), 5, AmmoType.MEDIUM, 30, 30, 5,
            5, 1, new GunBloom(1, 12, 4.5f, .75f, true, .75f, .75f),
            .85f, -.2f, -.5f, 3, 2/3d, 20, 20, true, new ThresholdDamageDropoff(new LinkedHashMap<>()).add(65, .65f)
            .add(50, .8f).add(25, 1), new GunSound(SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST, 5, .75f));

    public static final BulletItem MEDIUM_BULLET = new BulletItem(new FabricItemSettings(), AmmoType.MEDIUM);
    public static final BulletItem MEDIUM_BULLET2 = new BulletItem(new FabricItemSettings(), AmmoType.MEDIUM);
    public static final BulletItem HEAVY_BULLET = new BulletItem(new FabricItemSettings(), AmmoType.HEAVY);
    public static final GunAttachmentItem EPIC_ATTACHMENT = new GunAttachmentItem(new FabricItemSettings());
    public static final FragGrenadeItem FRAG_GRENADE_ITEM = new FragGrenadeItem(new FabricItemSettings());
    public static final TestStatAttachment FIRERATE_FAST = new TestStatAttachment(new FabricItemSettings());

    @Override
    public void onInitialize() {

        LOGGER.info("Simple Warfare initializing");

        QPackets.registerC2SPackets();

        Registry.register(Registries.ITEM, getIdentifier("ak47"), AK47);
        Registry.register(Registries.ITEM, getIdentifier("frag_nade"), FRAG_GRENADE_ITEM);
        Registry.register(Registries.ITEM, getIdentifier("medium_bullet"), MEDIUM_BULLET);
        Registry.register(Registries.ITEM, getIdentifier("medium_bullet2"), MEDIUM_BULLET2);
        Registry.register(Registries.ITEM, getIdentifier("heavy_bullet"), HEAVY_BULLET);
        Registry.register(Registries.ITEM, getIdentifier("epic_attachment"), EPIC_ATTACHMENT);
        Registry.register(Registries.ITEM, getIdentifier("fast_attachment"), FIRERATE_FAST);
    }
}
