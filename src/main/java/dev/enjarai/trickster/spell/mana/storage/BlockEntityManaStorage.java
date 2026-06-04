package dev.enjarai.trickster.spell.mana.storage;

import com.google.common.collect.ImmutableList;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class BlockEntityManaStorage<T extends BlockEntity & Inventory> extends CombinedStorage<ManaVariant, Storage<ManaVariant>> {
    public BlockEntityManaStorage(T blockEntity) {
        super(createStorages(blockEntity));
    }

    private static <T extends BlockEntity & Inventory> List<Storage<ManaVariant>> createStorages(T blockEntity) {
        var result = new ArrayList<Storage<ManaVariant>>();

        var inventory = InventoryStorage.of(blockEntity, null);
        for (var slot : inventory.getSlots()) {
            var storage = ManaStorage.ITEM.find(
                slot.getResource().toStack(),
                new ManaStorage.LookupContext(ContainerItemContext.ofSingleSlot(slot), blockEntity.getWorld())
            );

            if (storage != null) {
                result.add(storage);
            }
        }

        return ImmutableList.copyOf(result);
    }
}
