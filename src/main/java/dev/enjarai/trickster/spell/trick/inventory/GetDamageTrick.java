package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.slot.SlotFragment;
import dev.enjarai.trickster.spell.fragment.slot.VariantType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.component.DataComponentTypes;

import java.util.Optional;

public class GetDamageTrick extends Trick<GetDamageTrick> {
    public GetDamageTrick() {
        super(Pattern.of(4, 3, 2, 4, 5, 6, 4), Signature.of(FragmentType.SLOT, GetDamageTrick::get, FragmentType.NUMBER.optionalOfRet()));
    }

    public Optional<NumberFragment> get(SpellContext ctx, SlotFragment slot) {
        var resource = slot.getResource(this, ctx, VariantType.ITEM);
        var damage = resource.getComponentMap().get(DataComponentTypes.DAMAGE);
        return Optional.ofNullable(damage).map(NumberFragment::new);
    }
}
