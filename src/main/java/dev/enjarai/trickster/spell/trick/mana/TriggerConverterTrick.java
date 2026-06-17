package dev.enjarai.trickster.spell.trick.mana;

import java.util.Optional;

import dev.enjarai.trickster.item.ConverterItem;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.ItemInvalidBlunder;
import dev.enjarai.trickster.spell.blunder.OutOfRangeBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ManaFragment;
import dev.enjarai.trickster.spell.fragment.slot.SlotFragment;
import dev.enjarai.trickster.spell.fragment.slot.VariantType;
import dev.enjarai.trickster.spell.mana.type.Mana;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.util.Resultor;

public class TriggerConverterTrick extends Trick<TriggerConverterTrick> {
    public TriggerConverterTrick() {
        super(Pattern.of(), Signature.of(FragmentType.SLOT, TriggerConverterTrick::trigger, FragmentType.MANA.optionalOfRet()));
    }

    public Optional<ManaFragment> trigger(SpellContext ctx, SlotFragment slot) {
        var resource = slot.getResource(this, ctx, VariantType.ITEM);
        var range = ctx.source().getPos().distance(slot.getSourceOrCasterPos(this, ctx));

        if (range > 16) {
            throw new OutOfRangeBlunder(this, 16, range);
        }

        var item = resource.getItem();
        if (item instanceof ConverterItem converterItem) {
            final var resultor = new Resultor<Mana>();

            slot.applyItemModifier(this, ctx, stack -> {
                resultor.value = converterItem.convert(ctx, stack);
                return stack;
            });

            return Optional.ofNullable(resultor.value).map(ManaFragment::new);
        }

        throw new ItemInvalidBlunder(this);
    }
}
