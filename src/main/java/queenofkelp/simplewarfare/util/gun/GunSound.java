package queenofkelp.simplewarfare.util.gun;

import net.minecraft.sound.SoundEvent;

public class GunSound {

    public SoundEvent shootSound;
    public float volume;
    public float pitch;

    public GunSound(SoundEvent shootSound, float volume, float pitch) {
        this.shootSound = shootSound;
        this.volume = volume;
        this.pitch = pitch;
    }

}
