package dev.enjarai.trickster.data.mana;

import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.mana.storage.ManaVariant;

public class StormStableConversionCondition implements ManaConversionCondition {
    @Override
    public boolean test(SpellContext ctx, ManaVariant variant) {
        return false; // TODO
    }
}
