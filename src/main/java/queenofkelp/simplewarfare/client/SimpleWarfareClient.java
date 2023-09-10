package queenofkelp.simplewarfare.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import queenofkelp.simplewarfare.client.keybinds.QKeybinds;
import queenofkelp.simplewarfare.networking.QPackets;
import queenofkelp.simplewarfare.particle.BasicTracerParticle;
import queenofkelp.simplewarfare.registry.QParticles;


public class SimpleWarfareClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        QPackets.registerS2CPackets();

        QKeybinds.initialize();

        ParticleFactoryRegistry.getInstance().register(QParticles.SIMPLE_TRACER, BasicTracerParticle.Factory::new);
    }

}
