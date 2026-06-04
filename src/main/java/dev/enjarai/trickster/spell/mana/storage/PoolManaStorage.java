package dev.enjarai.trickster.spell.mana.storage;

import dev.enjarai.trickster.spell.mana.MutableManaPool;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.world.World;

public class PoolManaStorage extends SnapshotParticipant<Long> implements SingleSlotStorage<ManaVariant> {
    private final MutableManaPool pool;
    private final World world;

    public PoolManaStorage(MutableManaPool pool, World world) {
        this.pool = pool;
        this.world = world;
    }

    @Override
    public long insert(ManaVariant variant, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);

        long inserted = Math.min(maxAmount, pool.getMax(world) - pool.get(world));

        if (inserted > 0) {
            updateSnapshots(transaction);
            var leftovers = pool.refill(variant, inserted, world);
            return inserted - leftovers;
        }

        return 0;
    }

    @Override
    public long extract(ManaVariant variant, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);

        long extracted = Math.min(maxAmount, pool.get(world));

        if (extracted > 0) {
            updateSnapshots(transaction);
            var leftovers = pool.use(variant, extracted, world);
            return extracted - leftovers;
        }

        return 0;
    }

    @Override
    public boolean isResourceBlank() {
        return getResource().isBlank();
    }

    @Override
    public ManaVariant getResource() {
        return pool.getVariant(world);
    }

    @Override
    public long getAmount() {
        return pool.get(world);
    }

    @Override
    public long getCapacity() {
        return pool.getMax(world);
    }

    @Override
    protected Long createSnapshot() {
        return pool.get(world);
    }

    @Override
    protected void readSnapshot(Long snapshot) {
        pool.set(snapshot, world);
    }
}
