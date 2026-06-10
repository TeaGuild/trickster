package dev.enjarai.trickster.spell.mana.type;

import dev.enjarai.trickster.particle.ModParticles;
import net.minecraft.particle.ParticleEffect;

public class StormMana extends Mana {
    public StormMana(int color) {
        super(color);
    }

    @Override
    public ParticleEffect getParticle() {
        return ModParticles.STORM_WHITE;
    }
}
