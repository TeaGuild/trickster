package dev.enjarai.trickster.spell.mana.storage;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.item.component.ModComponents;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.world.World;

public interface ManaStorage {
    ItemApiLookup<Storage<ManaVariant>, LookupContext> ITEM = ItemApiLookup.get(Trickster.id("mana"), Storage.asClass(), LookupContext.class);

    static void register() {
        ManaStorage.ITEM.registerFallback((itemStack, lookupContext) -> {
            if (itemStack.contains(ModComponents.MANA)) {
                return new KnotManaStorage(lookupContext.itemContext(), lookupContext.world());
            }
            return null;
        });
    }

    record LookupContext(ContainerItemContext itemContext, World world) {

    }
}
