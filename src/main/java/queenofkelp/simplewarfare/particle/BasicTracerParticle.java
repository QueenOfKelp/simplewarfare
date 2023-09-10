package queenofkelp.simplewarfare.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;

public class BasicTracerParticle extends SpriteBillboardParticle {
    protected BasicTracerParticle(ClientWorld level, double xCoord, double yCoord, double zCoord,
                                  SpriteProvider spriteSet) {
        super(level, xCoord, yCoord, zCoord, 0, 0, 0);

        this.velocityMultiplier = .66f;

        this.velocityX = 0;
        this.velocityY = 0;
        this.velocityZ = 0;

        this.maxAge = 3;
        this.gravityStrength = 0;
        this.field_28787 = true;
        this.scale = .005f;
        this.red = 1f;
        this.green = 1f;
        this.blue = 1f;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public int getBrightness(float tint) {
        float f = ((float)this.age + tint) / (float)this.maxAge;
        f = MathHelper.clamp(f, 0.0F, 1.0F);
        int i = super.getBrightness(tint);
        int j = i & 255;
        int k = i >> 16 & 255;
        j += (int)(f * 15.0F * 16.0F);
        if (j > 240) {
            j = 240;
        }

        return j | k << 16;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider sprites;

        public Factory(SpriteProvider spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(DefaultParticleType particleType, ClientWorld level, double x, double y, double z,
                                       double dx, double dy, double dz) {
            BasicTracerParticle particle = new BasicTracerParticle(level, x, y, z, this.sprites);
            particle.setSprite(sprites);
            return particle;
        }
    }
}
