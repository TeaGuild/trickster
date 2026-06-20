package dev.enjarai.trickster.spell.trick.mana;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.slot.StorageFragment;
import dev.enjarai.trickster.spell.fragment.slot.VariantType;
import dev.enjarai.trickster.spell.mana.storage.ManaStorage;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.Signature;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;

public class GetMaxManaInSlotTrick extends Trick<GetMaxManaInSlotTrick> {
    public GetMaxManaInSlotTrick() {
        super(Pattern.of(3, 4, 5, 8, 6, 3, 0, 4, 2, 0, 1, 2, 5), Signature.of(ArgType.simple(StorageFragment.class), GetMaxManaInSlotTrick::run, FragmentType.NUMBER));
    }

    public NumberFragment run(SpellContext ctx, StorageFragment storage) {
        long result = 0;

        for (var view : storage.getStorage(this, ctx, VariantType.ITEM).nonEmptyViews()) {
            var manaStorage = ManaStorage.ITEM.find(view.getResource().toStack(), new ManaStorage.LookupContext(
                ContainerItemContext.withConstant(view.getResource(), view.getAmount()),
                ctx.source().getWorld()
            ));

            if (manaStorage != null) {
                for (var view2 : manaStorage) {
                    result += view.getAmount() * view2.getCapacity();
                }
            }
        }

        return NumberFragment.ofMana(result);
    }
}
