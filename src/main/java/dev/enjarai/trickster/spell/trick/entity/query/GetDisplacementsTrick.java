package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import net.minecraft.entity.Entity;

public class GetDisplacementsTrick extends AbstractEntityQueryTrick<Entity, NumberFragment> {
    public GetDisplacementsTrick() {
        super(Pattern.of(1, 6, 3, 1, 5, 8, 1), Entity.class, FragmentType.NUMBER);
    }

    @Override
    public NumberFragment run(SpellContext ctx, Entity entity) {
        return new NumberFragment(entity.getComponent(ModEntityComponents.DISPLACEMENT).getAvailableSlots());
    }
}
