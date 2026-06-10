package dev.enjarai.trickster.spell.mana.type;

import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import net.minecraft.particle.ParticleEffect;

import java.util.Optional;

public class StormMana extends Mana {
    public StormMana(int color) {
        super(color);
    }

    @Override
    public ParticleEffect getParticle() {
        return ModParticles.STORM_WHITE;
    }

    @Override
    public Optional<ManaVariant> mix(ManaVariant left, long amountLeft, ManaVariant right, long amountRight) {
        return Optional.empty(); //TODO: impl mixing for storm mana
    }
}
