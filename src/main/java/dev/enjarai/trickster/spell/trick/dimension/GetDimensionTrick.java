package dev.enjarai.trickster.spell.trick.dimension;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.DimensionFragment;
import dev.enjarai.trickster.spell.fragment.DisplaceFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class GetDimensionTrick extends Trick<GetDimensionTrick> {
    public GetDimensionTrick() {
        super(Pattern.of(4, 0, 1, 4, 3, 6, 5, 2, 4), Signature.of(GetDimensionTrick::fromCaster, FragmentType.DIMENSION));
        overload(Signature.of(FragmentType.DISPLACE, GetDimensionTrick::fromDisplacement, FragmentType.DIMENSION));
    }

    public DimensionFragment fromCaster(SpellContext ctx) {
        return DimensionFragment.of(ctx.source().getWorld());
    }

    public DimensionFragment fromDisplacement(SpellContext ctx, DisplaceFragment displacement) {
        return new DimensionFragment(displacement.sourceWorld());
    }
}
