package queenofkelp.simplewarfare.registry;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import queenofkelp.simplewarfare.SimpleWarfare;

public class QParticles {
    public static final DefaultParticleType SIMPLE_TRACER = FabricParticleTypes.simple();

    public static void registerParticles() {
        Registry.register(Registries.PARTICLE_TYPE, SimpleWarfare.getIdentifier("simple_tracer"), SIMPLE_TRACER);
    }

}

