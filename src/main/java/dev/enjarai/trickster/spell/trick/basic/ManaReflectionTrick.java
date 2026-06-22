package dev.enjarai.trickster.spell.trick.basic;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class ManaReflectionTrick extends Trick<ManaReflectionTrick> {
    public ManaReflectionTrick() {
        super(Pattern.of(5, 7, 3, 1, 5, 2, 4, 0, 3, 4, 5), Signature.of(ManaReflectionTrick::run, FragmentType.NUMBER));
    }

    public NumberFragment run(SpellContext ctx) {
        long result = 0;

        for (var view : ctx.getManaStorage().nonEmptyViews()) {
            result += view.getAmount();
        }

        return NumberFragment.ofMana(result);
    }
}
