package dev.enjarai.trickster.util;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

public class StorageViewContainerContext implements ContainerItemContext {
    private final StorageView<ItemVariant> view;
    private final Storage<ItemVariant> storage;

    public StorageViewContainerContext(StorageView<ItemVariant> view, Storage<ItemVariant> storage) {
        this.view = view;
        this.storage = storage;
    }

    @Override
    public SingleSlotStorage<ItemVariant> getMainSlot() {
        return new SingleSlotStorage<ItemVariant>() {
            @Override
            public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
                return 0;
            }

            @Override
            public boolean supportsInsertion() {
                return false;
            }

            @Override
            public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
                return view.extract(resource, maxAmount, transaction);
            }

            @Override
            public boolean isResourceBlank() {
                return view.isResourceBlank();
            }

            @Override
            public ItemVariant getResource() {
                return view.getResource();
            }

            @Override
            public long getAmount() {
                return view.getAmount();
            }

            @Override
            public long getCapacity() {
                return view.getCapacity();
            }
        };
    }

    @Override
    public long insertOverflow(ItemVariant itemVariant, long maxAmount, TransactionContext transactionContext) {
        return storage.insert(itemVariant, maxAmount, transactionContext);
    }

    @Override
    public @UnmodifiableView List<SingleSlotStorage<ItemVariant>> getAdditionalSlots() {
        return List.of();
    }
}
