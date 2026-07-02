package dev.enjarai.trickster.spell.trick.mana;

import dev.enjarai.trickster.data.DataLoader;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.OutOfRangeBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ManaFragment;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.slot.SlotFragment;
import dev.enjarai.trickster.spell.fragment.slot.VariantType;
import dev.enjarai.trickster.spell.mana.ManaPool;
import dev.enjarai.trickster.spell.mana.storage.ManaStorage;
import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

public class ConvertManaTrick extends Trick<ConvertManaTrick> {
    public ConvertManaTrick() {
        super(
            Pattern.of(0, 1, 2, 5, 0).add(Pattern.of(8, 7, 6, 3, 8)),
            Signature.of(FragmentType.SLOT, FragmentType.MANA, ConvertManaTrick::convert, FragmentType.NUMBER)
        );
    }

    public NumberFragment convert(SpellContext ctx, SlotFragment slot, ManaFragment mana) {
        var range = ctx.source().distanceTo(slot.getSourceOrCasterPos(this, ctx));

        if (range > 16) {
            throw new OutOfRangeBlunder(this, 16.0, range);
        }

        var slotStorage = slot.getStorage(this, ctx, VariantType.ITEM);
        var manaStorage = ManaStorage.ITEM.find(
            slotStorage.getResource().toStack(),
            new ManaStorage.LookupContext(ContainerItemContext.ofSingleSlot(slotStorage), ctx.source().getWorld())
        );

        if (manaStorage == null) {
            return new NumberFragment(0);
        }

        long totalConverted = 0;

        for (var view : manaStorage.nonEmptyViews()) {
            var conversion = DataLoader.getManaLoader().getManaConversion(view.getResource().getMana(), mana.mana());

            if (conversion == null) continue;

            if (conversion.condition().map(cond -> cond.test(ctx, view.getResource())).orElse(true)) {
                try (var trans = Transaction.openOuter()) {
                    long extracted = view.extract(view.getResource(), Long.MAX_VALUE, trans);
                    long converted = (long) ((float) extracted * conversion.multiplier());

                    if (0 < manaStorage.insert(ManaVariant.of(conversion.to()), converted, trans)) {
                        trans.commit();
                        totalConverted += converted;
                    }
                }
            }
        }

        return new NumberFragment((double) totalConverted / ManaPool.MANA_SCALE);
    }
}
