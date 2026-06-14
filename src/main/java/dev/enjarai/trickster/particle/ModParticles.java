package dev.enjarai.trickster.particle;

import dev.enjarai.trickster.Trickster;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.jetbrains.annotations.ApiStatus;

public class ModParticles {
    public static final ParticleType<SpellParticleOptions> SPELL = register("spell",
        FabricParticleTypes.complex(SpellParticleOptions.CODEC, SpellParticleOptions.PACKET_CODEC));
    public static final ParticleType<SpellParticleOptions> STORM = register("storm",
        FabricParticleTypes.complex(SpellParticleOptions.CODEC, SpellParticleOptions.PACKET_CODEC));
    public static final ParticleType<HighlightParticleOptions> HIGHLIGHT_BLOCK = register("protected_block",
        FabricParticleTypes.complex(HighlightParticleOptions.CODEC, HighlightParticleOptions.PACKET_CODEC));
    public static final ParticleType<LightningParticleOptions> LIGHTNING = register("lightning",
        FabricParticleTypes.complex(LightningParticleOptions.CODEC, LightningParticleOptions.PACKET_CODEC));

    public static final HighlightParticleOptions PROTECTED_BLOCK = new HighlightParticleOptions(-0x66666601);
    public static final SpellParticleOptions SPELL_WHITE = new SpellParticleOptions(0xffffff);
    public static final SpellParticleOptions STORM_WHITE = new SpellParticleOptions(0xffffff, STORM);

    @ApiStatus.Internal
    public static <T extends ParticleEffect> ParticleType<T> register(String id, ParticleType<T> particleType) {
        return Registry.register(Registries.PARTICLE_TYPE, Trickster.id(id), particleType);
    }

    public static void register() {

    }
}
