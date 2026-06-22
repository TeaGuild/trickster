package dev.enjarai.trickster.data.mana;

import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import dev.enjarai.trickster.spell.mana.type.component.ManaComponents;

public class StormStableConversionCondition implements ManaConversionCondition {
    @Override
    public boolean test(SpellContext ctx, ManaVariant variant) {
        var component = variant.getComponentMap().get(ManaComponents.STABILITY);
        if (component == null) {
            return false;
        }

        return component.getStability(ctx.source().getWorld()) > 0.95f;
    }
}
