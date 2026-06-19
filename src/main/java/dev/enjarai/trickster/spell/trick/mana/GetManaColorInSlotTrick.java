package dev.enjarai.trickster.spell.trick.mana;

import dev.enjarai.trickster.item.KnotItem;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.ItemInvalidBlunder;
import dev.enjarai.trickster.spell.fragment.ColorFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.slot.SlotFragment;
import dev.enjarai.trickster.spell.fragment.slot.VariantType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class GetManaColorInSlotTrick extends Trick<GetManaColorInSlotTrick> {
    public GetManaColorInSlotTrick() {
        super(Pattern.of(4, 2, 1, 0, 2, 7, 0, 4, 5, 8, 6, 3, 4), Signature.of(FragmentType.SLOT, GetManaColorInSlotTrick::get, FragmentType.COLOR));
    }

    public ColorFragment get(SpellContext ctx, SlotFragment slot) {
        var variant = slot.getResource(this, ctx, VariantType.ITEM);

        //TODO: this is bad and incompatible but it should work for exactly what we need and nothing else
        if (variant.getItem() instanceof KnotItem) {
            return new ColorFragment(variant.getItem().getItemBarColor(variant.toStack()));
        }

        throw new ItemInvalidBlunder(this);
    }
}
