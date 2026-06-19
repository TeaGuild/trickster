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

import java.util.concurrent.atomic.AtomicReference;

public class TriggerConverterTrick extends Trick<TriggerConverterTrick> {
    public TriggerConverterTrick() {
        super(Pattern.of(3, 2, 1, 0, 3).add(Pattern.of(5, 6, 7, 8, 5)), Signature.of(FragmentType.SLOT, TriggerConverterTrick::trigger, FragmentType.MANA.optionalOfRet()));
    }

    public Optional<ManaFragment> trigger(SpellContext ctx, SlotFragment slot) {
        var resource = slot.getResource(this, ctx, VariantType.ITEM);
        var range = ctx.source().getPos().distance(slot.getSourceOrCasterPos(this, ctx));

        if (range > 16) {
            throw new OutOfRangeBlunder(this, 16, range);
        }

        var item = resource.getItem();
        if (item instanceof ConverterItem converterItem) {
            AtomicReference<Mana> result = new AtomicReference<>();

            slot.applyItemModifier(this, ctx, stack -> {
                result.set(converterItem.convert(ctx, stack));
                return stack;
            });

            return Optional.ofNullable(result.get()).map(ManaFragment::new);
        }

        throw new ItemInvalidBlunder(this);
    }
}
