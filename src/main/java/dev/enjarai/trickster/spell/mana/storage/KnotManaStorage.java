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

    public ManaComponent component(ItemVariant variant) {
        return variant.getComponentMap().getOrDefault(ModComponents.MANA, ManaComponent.EMPTY);
    }

    private boolean isStillValid(ItemVariant variant) {
        return variant.getItem() == originalItem;
    }

    protected boolean isResourceBlank(ItemVariant variant) {
        return getResource(variant).isBlank();
    }

    protected ManaVariant getResource(ItemVariant variant) {
        return component(variant).pool().getVariant(world);
    }

    protected long getAmount(ItemVariant variant) {
        return component(variant).pool().get(world);
    }

    protected long getCapacity(ItemVariant variant) {
        return component(variant).pool().getMax(world);
    }

    protected boolean setMana(ItemVariant variant, ManaVariant mana, long amount, TransactionContext transaction) {
        var component = component(variant);
        var pool = component.pool().makeClone(world);
        pool.set(amount, world);
        pool.setVariant(mana, world);

        ItemVariant newVariant = variant.withComponentChanges(ComponentChanges.builder()
            .add(ModComponents.MANA, component.with(pool))
            .build());

        return ctx.exchange(newVariant, 1, transaction) == 1;
    }

    @Override
    public long insert(ManaVariant mana, long amount, TransactionContext transaction) {
        StoragePreconditions.notNegative(amount);

        var variant = ctx.getItemVariant();

        if (!isStillValid(variant)) return 0;

        if (getResource(variant).equals(mana) || isResourceBlank(variant)) {
            long currentMana = getAmount(variant);
            long inserted = Math.min(amount, getCapacity(variant) - currentMana);

            if (inserted > 0) {
                currentMana += inserted;

                if (!setMana(variant, mana, currentMana, transaction)) return 0;

                return inserted;
            }
        }

        return 0;
    }

    @Override
    public long extract(ManaVariant mana, long amount, TransactionContext transaction) {
        StoragePreconditions.notNegative(amount);

        var variant = ctx.getItemVariant();

        if (!isStillValid(variant)) return 0;

        if (getResource(variant).equals(mana)) {
            long currentMana = getAmount(variant);
            long extracted = Math.min(currentMana, amount);

            if (extracted > 0) {
                currentMana -= extracted;

                if (!setMana(variant, currentMana == 0 ? ManaVariant.EMPTY : mana, currentMana, transaction)) return 0;

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
        return getResource(ctx.getItemVariant());
    }

    @Override
    public long getAmount() {
        return getAmount(ctx.getItemVariant());
    }

    @Override
    public long getCapacity() {
        return getCapacity(ctx.getItemVariant());
    }
}
