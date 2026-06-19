package dev.enjarai.trickster.spell.trick.mana;

import java.util.ArrayList;
import java.util.List;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.ColorFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.slot.StorageFragment;
import dev.enjarai.trickster.spell.fragment.slot.VariantType;
import dev.enjarai.trickster.spell.mana.storage.ManaStorage;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.Signature;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;

public class GetManaColorInSlotTrick extends Trick<GetManaColorInSlotTrick> {
    public GetManaColorInSlotTrick() {
        super(Pattern.of(4, 2, 1, 0, 2, 7, 0, 4, 5, 8, 6, 3, 4), Signature.of(ArgType.simple(StorageFragment.class), GetManaColorInSlotTrick::get, FragmentType.COLOR.listOfRet()));
    }

    public List<ColorFragment> get(SpellContext ctx, StorageFragment storage) {
        var results = new ArrayList<ColorFragment>();

        for (var view : storage.getStorage(this, ctx, VariantType.ITEM).nonEmptyViews()) {
            var manaStorage = ManaStorage.ITEM.find(view.getResource().toStack(), new ManaStorage.LookupContext(
                ContainerItemContext.withConstant(view.getResource(), view.getAmount()),
                ctx.source().getWorld()
            ));

            if (manaStorage != null) {
                for (var view2 : manaStorage.nonEmptyViews()) {
                    var color = ColorFragment.fromRGB(view2.getResource().getColor());

                    if (!results.contains(color)) {
                        results.add(color);
                    }
                }
            }
        }

        return results;
    }
}
