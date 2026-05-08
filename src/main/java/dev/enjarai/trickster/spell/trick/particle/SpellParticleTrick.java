package dev.enjarai.trickster.spell.trick.particle;

import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.particle.SpellParticleOptions;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.ColorFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import org.joml.Vector3d;

import java.util.List;
import java.util.Optional;

public class SpellParticleTrick extends Trick<SpellParticleTrick> {
    public SpellParticleTrick() {
        super(Pattern.of(0, 1, 2, 0, 4, 8, 5, 4, 3, 6, 7, 8),
                Signature.of(FragmentType.VECTOR.variadicOfArg().require().unpack(), SpellParticleTrick::run, FragmentType.VECTOR));
        overload(Signature.of(FragmentType.VECTOR.optionalOfArg(), FragmentType.COLOR.optionalOfArg(), FragmentType.VECTOR.variadicOfArg().require().unpack(),
                SpellParticleTrick::runColored, FragmentType.VECTOR));
    }

    public VectorFragment run(SpellContext ctx, List<VectorFragment> positions) {
        for (var pos : positions) {
            ctx.source().getWorld().spawnParticles(
                    ModParticles.SPELL_WHITE, pos.x(), pos.y(), pos.z(),
                    0, 0, 0, 0, 0
            );
        }

        return positions.getFirst();
    }

    public VectorFragment runColored(SpellContext ctx, Optional<VectorFragment> optionalDelta, Optional<ColorFragment> optionalColor, List<VectorFragment> positions) {
        var delta = optionalDelta.orElse(new VectorFragment(new Vector3d(0, 0, 0)));
        var particle = optionalColor.isPresent() ? new SpellParticleOptions(optionalColor.get().color()) : ModParticles.SPELL_WHITE;

        for (var pos : positions) {
            ctx.source().getWorld().spawnParticles(particle, pos.x(), pos.y(), pos.z(),
                    0, delta.x(), delta.y(), delta.z(), 1
            );
        }

        return positions.getFirst();
    }
}
