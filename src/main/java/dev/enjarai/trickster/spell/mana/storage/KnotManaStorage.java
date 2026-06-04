package dev.enjarai.trickster.spell.mana.storage;

import dev.enjarai.trickster.item.component.ManaComponent;
import dev.enjarai.trickster.item.component.ModComponents;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.component.ComponentChanges;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class KnotManaStorage implements SingleSlotStorage<ManaVariant> {
    private final ContainerItemContext ctx;
    private final World world;
    private final Item originalItem;

    public KnotManaStorage(ContainerItemContext ctx, World world) {
        this.ctx = ctx;
        this.world = world;

        this.originalItem = ctx.getItemVariant().getItem();
    }

    public ManaComponent component() {
        return ctx.getItemVariant().getComponentMap().getOrDefault(ModComponents.MANA, ManaComponent.EMPTY);
    }

    private boolean isStillValid() {
        return ctx.getItemVariant().getItem() == originalItem;
    }

    protected long getMana() {
        return component().pool().get(world);
    }

    protected boolean setMana(ManaVariant mana, long amount, TransactionContext transaction) {
        var component = component();
        var pool = component.pool().makeClone(world);
        pool.set(amount, world);
        pool.setVariant(mana, world);

        ItemVariant newVariant = ctx.getItemVariant().withComponentChanges(ComponentChanges.builder()
            .add(ModComponents.MANA, component.with(pool))
            .build());

        return ctx.exchange(newVariant, 1, transaction) == 1;
    }

    @Override
    public long insert(ManaVariant mana, long amount, TransactionContext transaction) {
        StoragePreconditions.notNegative(amount);

        if (!isStillValid()) return 0;

        if (getResource().equals(mana) || isResourceBlank()) {
            long currentMana = getMana();
            int inserted = (int) Math.min(amount, getCapacity() - currentMana);

            if (inserted > 0) {
                currentMana += inserted;

                if (!setMana(mana, currentMana, transaction)) return 0;

                return inserted;
            }
        }

        return 0;
    }

    @Override
    public long extract(ManaVariant mana, long amount, TransactionContext transaction) {
        StoragePreconditions.notNegative(amount);

        if (!isStillValid()) return 0;

        if (getResource().equals(mana)) {
            long currentMana = getMana();
            long extracted = Math.min(currentMana, amount);

            if (extracted > 0) {
                currentMana -= extracted;

                if (!setMana(currentMana == 0 ? ManaVariant.EMPTY : mana, currentMana, transaction)) return 0;

                return extracted;
            }
        }

        return 0;
    }

    @Override
    public boolean isResourceBlank() {
        return getResource().isBlank();
    }

    @Override
    public ManaVariant getResource() {
        return component().pool().getVariant(world);
    }

    @Override
    public long getAmount() {
        return getMana();
    }

    @Override
    public long getCapacity() {
        return component().pool().getMax(world);
    }
}
