package dev.enjarai.trickster.spell.trick.mana;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ManaFragment;
import dev.enjarai.trickster.spell.fragment.slot.StorageFragment;
import dev.enjarai.trickster.spell.fragment.slot.VariantType;
import dev.enjarai.trickster.spell.mana.storage.ManaStorage;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.Signature;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;

import java.util.ArrayList;
import java.util.List;

public class GetManaTypeInSlotTrick extends Trick<GetManaTypeInSlotTrick> {
    public GetManaTypeInSlotTrick() {
        super(Pattern.of(4, 0, 2, 4, 5, 8, 6, 3, 4), Signature.of(ArgType.simple(StorageFragment.class), GetManaTypeInSlotTrick::run, FragmentType.MANA.listOfRet()));
    }

    public List<ManaFragment> run(SpellContext ctx, StorageFragment storage) {
        var results = new ArrayList<ManaFragment>();

        for (var view : storage.getStorage(this, ctx, VariantType.ITEM).nonEmptyViews()) {
            var manaStorage = ManaStorage.ITEM.find(view.getResource().toStack(), new ManaStorage.LookupContext(
                ContainerItemContext.withConstant(view.getResource(), view.getAmount()),
                ctx.source().getWorld()
            ));

            if (manaStorage != null) {
                for (var view2 : manaStorage.nonEmptyViews()) {
                    var mana = new ManaFragment(view2.getResource().getMana());

                    if (!results.contains(mana)) {
                        results.add(mana);
                    }
                }
            }
        }

        return results;
    }
}
