package dev.enjarai.trickster.spell.mana.storage;

import com.google.common.collect.ImmutableList;
import dev.enjarai.trickster.util.SlotReferenceStorage;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class PlayerInventoryManaStorage extends CombinedStorage<ManaVariant, Storage<ManaVariant>> {
    public PlayerInventoryManaStorage(PlayerEntity player) {
        super(createStorages(player));
    }

    private static List<Storage<ManaVariant>> createStorages(PlayerEntity player) {
        var result = new ArrayList<Storage<ManaVariant>>();

        var playerAccessories = player.accessoriesCapability();
        for (var equipped : playerAccessories.getAllEquipped()) {
            var referenceStorage = new SlotReferenceStorage(equipped.reference());
            var storage = ManaStorage.ITEM.find(
                referenceStorage.getResource().toStack(),
                new ManaStorage.LookupContext(ContainerItemContext.ofPlayerSlot(player, referenceStorage), player.getWorld())
            );

            if (storage != null) {
                result.add(storage);
            }
        }

        var playerStorage = PlayerInventoryStorage.of(player);
        for (var slot : playerStorage.getSlots()) {
            var storage = ManaStorage.ITEM.find(
                slot.getResource().toStack(),
                new ManaStorage.LookupContext(ContainerItemContext.ofPlayerSlot(player, slot), player.getWorld())
            );

            if (storage != null) {
                result.add(storage);
            }
        }

        return ImmutableList.copyOf(result);
    }
}
