package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.slot.SlotFragment;
import dev.enjarai.trickster.spell.fragment.slot.VariantType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.util.OptionalUtils;
import net.minecraft.component.DataComponentTypes;

import java.util.Optional;

public class GetDurabilityTrick extends Trick<GetDurabilityTrick> {
    public GetDurabilityTrick() {
        super(Pattern.of(4, 3, 2, 4, 5, 6, 4), Signature.of(FragmentType.SLOT, GetDurabilityTrick::get, FragmentType.NUMBER.optionalOfRet()));
    }

    public Optional<NumberFragment> get(SpellContext ctx, SlotFragment slot) {
        var resource = slot.getResource(this, ctx, VariantType.ITEM);
        var damage = resource.getComponentMap().get(DataComponentTypes.DAMAGE);
        var maxDamage = resource.getComponentMap().get(DataComponentTypes.MAX_DAMAGE);
        return OptionalUtils.lift2(
            (d, md) -> md - d,
            Optional.ofNullable(damage),
            Optional.ofNullable(maxDamage)
        ).map(NumberFragment::new);
    }
}
