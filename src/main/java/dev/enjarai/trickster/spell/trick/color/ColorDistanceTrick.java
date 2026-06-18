package dev.enjarai.trickster.spell.trick.color;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.ColorFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.util.ColorUtil;

public class ColorDistanceTrick extends DistortionTrick<ColorDistanceTrick> {
    public ColorDistanceTrick() {
        super(Pattern.of(3, 4, 5), Signature.of(FragmentType.COLOR, FragmentType.COLOR, ColorDistanceTrick::distance, FragmentType.NUMBER));
    }

    public NumberFragment distance(SpellContext ctx, ColorFragment left, ColorFragment right) {
        return new NumberFragment(ColorUtil.distance(left.color(), right.color()));
    }
}
