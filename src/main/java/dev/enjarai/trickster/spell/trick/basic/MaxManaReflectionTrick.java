package dev.enjarai.trickster.spell.trick.basic;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class MaxManaReflectionTrick extends Trick<MaxManaReflectionTrick> {
    public MaxManaReflectionTrick() {
        super(Pattern.of(2, 5, 7, 3, 1, 5, 4, 3, 0, 4, 2, 1, 0), Signature.of(MaxManaReflectionTrick::run, FragmentType.NUMBER));
    }

    public NumberFragment run(SpellContext ctx) {
        long result = 0;

        for (var view : ctx.getManaStorage().nonEmptyViews()) {
            result += view.getCapacity();
        }

        return NumberFragment.ofMana(result);
    }
}
