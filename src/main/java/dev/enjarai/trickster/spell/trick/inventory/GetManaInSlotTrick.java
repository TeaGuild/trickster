package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.slot.SlotFragment;
import dev.enjarai.trickster.spell.fragment.slot.VariantType;
import dev.enjarai.trickster.spell.mana.storage.ManaStorage;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;

import java.util.List;

public class GetManaInSlotTrick extends Trick<GetManaInSlotTrick> {
    public GetManaInSlotTrick() {
        super(Pattern.of(3, 4, 5, 2, 4, 0, 3, 6, 8, 5), Signature.of(FragmentType.SLOT.variadicOfArg().unpack(), GetManaInSlotTrick::run, FragmentType.NUMBER));
    }

    public NumberFragment run(SpellContext ctx, List<SlotFragment> slots) {
        long result = 0;

        for (var slot : slots) {
            var slotStorage = slot.getStorage(this, ctx, VariantType.ITEM);
            var manaStorage = ManaStorage.ITEM.find(slotStorage.getResource().toStack(), new ManaStorage.LookupContext(
                ContainerItemContext.ofSingleSlot(slotStorage),
                ctx.source().getWorld()
            ));
            if (manaStorage != null) {
                for (var view : manaStorage.nonEmptyViews()) {
                    result += view.getAmount();
                }
            }
        }

        return NumberFragment.ofMana(result);
    }
}
