package dev.enjarai.trickster.spell.mana.type;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import io.wispforest.endec.Endec;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.Optional;

public class Mana {
    public static final Endec<Mana> ENDEC = MinecraftEndecs.ofRegistry(Manae.REGISTRY);

    private final int color;

    public Mana(int color) {
        this.color = color;
    }

    public boolean isEmpty() {
        return this == Manae.EMPTY;
    }

    public int getColor() {
        return this.color;
    }

    public ParticleEffect getParticle() {
        return ModParticles.SPELL_WHITE;
    }

    public MutableText getName() {
        var id = Manae.REGISTRY.getId(this);
        if (id == null) {
            return Text.literal("Unregistered");
        }
        return Text.translatable(Trickster.MOD_ID + ".mana." + id.getNamespace() + "." + id.getPath()).withColor(color);
    }

    public Optional<ManaVariant> mix(ManaVariant left, long amountLeft, ManaVariant right, long amountRight) {
        return Optional.empty();
    }
}
