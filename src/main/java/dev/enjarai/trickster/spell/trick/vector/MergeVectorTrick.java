package dev.enjarai.trickster.spell.trick.vector;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.Optional;

import org.joml.Vector3d;

public class MergeVectorTrick extends DistortionTrick<MergeVectorTrick> {
    public MergeVectorTrick() {
        super(Pattern.of(1, 3, 4, 5, 1, 4, 7), Signature.of(FragmentType.NUMBER.optionalOfArg(), FragmentType.NUMBER.optionalOfArg(), FragmentType.NUMBER.optionalOfArg(), MergeVectorTrick::merge, FragmentType.VECTOR));
    }

    public VectorFragment merge(SpellContext ctx, Optional<NumberFragment> x, Optional<NumberFragment> y, Optional<NumberFragment> z) {
        return new VectorFragment(new Vector3d(x.map(NumberFragment::number).orElse(0d), y.map(NumberFragment::number).orElse(0d), z.map(NumberFragment::number).orElse(0d)));
    }
}
