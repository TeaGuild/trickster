package dev.enjarai.trickster.spell.trick.mana;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ManaFragment;
import dev.enjarai.trickster.spell.fragment.slot.SlotFragment;
import dev.enjarai.trickster.spell.fragment.slot.VariantType;
import dev.enjarai.trickster.spell.mana.storage.ManaStorage;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;

import java.util.ArrayList;
import java.util.List;

public class GetManaTypeInSlotTrick extends Trick<GetManaTypeInSlotTrick> {
    public GetManaTypeInSlotTrick() {
        super(Pattern.of(4, 0, 2, 4, 5, 8, 6, 3, 4), Signature.of(FragmentType.SLOT.variadicOfArg().unpack(), GetManaTypeInSlotTrick::run, FragmentType.MANA.listOfRet()));
    }

    public List<ManaFragment> run(SpellContext ctx, List<SlotFragment> slots) {
        var results = new ArrayList<ManaFragment>();

        for (var slot : slots) {
            var slotStorage = slot.getStorage(this, ctx, VariantType.ITEM);
            var manaStorage = ManaStorage.ITEM.find(slotStorage.getResource().toStack(), new ManaStorage.LookupContext(
                ContainerItemContext.ofSingleSlot(slotStorage),
                ctx.source().getWorld()
            ));
            if (manaStorage != null) {
                for (var view : manaStorage.nonEmptyViews()) {
                    var mana = new ManaFragment(view.getResource().getMana());

                    if (!results.contains(mana)) {
                        results.add(mana);
                    }
                }
            }
        }

        return results;
    }
}
