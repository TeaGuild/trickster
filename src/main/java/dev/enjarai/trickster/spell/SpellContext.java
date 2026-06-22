package dev.enjarai.trickster.spell;

import dev.enjarai.trickster.advancement.criterion.ModCriteria;
import dev.enjarai.trickster.spell.blunder.*;
import dev.enjarai.trickster.spell.execution.ExecutionState;
import dev.enjarai.trickster.spell.execution.TickData;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.fragment.slot.SlotFragment;
import dev.enjarai.trickster.spell.fragment.slot.StorageSource;
import dev.enjarai.trickster.spell.fragment.slot.VariantType;
import dev.enjarai.trickster.spell.mana.ManaPool;
import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import dev.enjarai.trickster.spell.mana.type.Manae;
import dev.enjarai.trickster.spell.trick.Trick;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Optional;
import java.util.function.Predicate;

public record SpellContext(ExecutionState state, SpellSource source, TickData data) {
    public void useScaledMana(Trick<?> trickSource, double amount) throws BlunderException {
        useScaledMana(trickSource, ManaVariant.of(Manae.TRADITIONAL), amount, null);
    }

    public void useScaledMana(Trick<?> trickSource, double amount, TransactionContext trans) throws BlunderException {
        useScaledMana(trickSource, ManaVariant.of(Manae.TRADITIONAL), amount, trans);
    }

    public void useScaledMana(Trick<?> trickSource, ManaVariant variant, double amount) throws BlunderException {
        useScaledMana(trickSource, variant, amount, null);
    }

    public void useScaledMana(Trick<?> trickSource, ManaVariant variant, double amount, TransactionContext trans) throws BlunderException {
        useScaledMana(trickSource, variant2 -> variant2.equals(variant), amount, trans);
    }

    public void useScaledMana(Trick<?> trickSource, Predicate<ManaVariant> predicate, double amount) throws BlunderException {
        useScaledMana(trickSource, predicate, amount, null);
    }

    public void useScaledMana(Trick<?> trickSource, Predicate<ManaVariant> predicate, double amount, TransactionContext trans) throws BlunderException {
        var actual = (long) (amount * ManaPool.MANA_SCALE);

        if (!data.canUseMana()) {
            throw new NotEnoughManaBlunder(trickSource, actual);
        }

        try {
            state.useMana(trickSource, this, predicate, actual, trans);
        } catch (NotEnoughManaBlunder blunder) {
            source.getPlayer().ifPresent(ModCriteria.MANA_OVERFLUX::trigger);
            throw blunder;
        }

        source.getPlayer().ifPresent((player) -> ModCriteria.MANA_USED.trigger(player, actual));
    }

    public void checkScaledMana(Trick<?> trickSource, double amount) throws BlunderException {
        checkScaledMana(trickSource, ManaVariant.of(Manae.TRADITIONAL), amount, null);
    }

    public void checkScaledMana(Trick<?> trickSource, ManaVariant variant, double amount) throws BlunderException {
        checkScaledMana(trickSource, variant, amount, null);
    }

    public void checkScaledMana(Trick<?> trickSource, double amount, TransactionContext trans) throws BlunderException {
        checkScaledMana(trickSource, ManaVariant.of(Manae.TRADITIONAL), amount, trans);
    }

    public void checkScaledMana(Trick<?> trickSource, ManaVariant variant, double amount, TransactionContext trans) throws BlunderException {
        var actual = (long) (amount * ManaPool.MANA_SCALE);

        try (var trans2 = Transaction.openNested(trans)) {
            if (source.getManaStorage().extract(variant, actual, trans2) != actual) {
                source.getPlayer().ifPresent(ModCriteria.MANA_OVERFLUX::trigger);
                throw new NotEnoughManaBlunder(trickSource, actual);
            }
        }
    }

    public void checkScaledMana(Trick<?> trickSource, Predicate<ManaVariant> predicate, double amount) throws BlunderException {
        checkScaledMana(trickSource, predicate, amount, null);
    }

    public void checkScaledMana(Trick<?> trickSource, Predicate<ManaVariant> predicate, double amount, TransactionContext trans) throws BlunderException {
        var actual = (long) (amount * ManaPool.MANA_SCALE);

        try (var trans2 = Transaction.openNested(trans)) {
            long extracted = 0;

            for (var view : source.getManaStorage().nonEmptyViews()) {
                var variant = view.getResource();
                if (!predicate.test(variant)) {
                    continue;
                }

                extracted += view.extract(variant, actual - extracted, trans2);
            }

            if (extracted != actual) {
                source.getPlayer().ifPresent(ModCriteria.MANA_OVERFLUX::trigger);
                throw new NotEnoughManaBlunder(trickSource, actual);
            }
        }
    }

    public Storage<ManaVariant> getManaStorage() {
        return state().tryOverridePool(source.getManaStorage(), source.getWorld());
    }

    // I am disappointed in myself for having written this.
    // Maybe I'll clean it up one day. -- Aurora D.
    public Optional<ItemStack> getStack(Trick<?> trick, Optional<SlotFragment> optionalSlot, Predicate<ItemStack> validator) throws BlunderException {
        ItemStack result = null;

        if (optionalSlot.isPresent()) {
            var slot = optionalSlot.get().getStorage(trick, this, VariantType.ITEM);
            var resource = slot.getResource();

            if (!validator.test(resource.toStack())) {
                throw new ItemInvalidBlunder(trick);
            }

            try (var trans = Transaction.openOuter()) {
                if (slot.extract(resource, 1, trans) == 1) {
                    result = resource.toStack();
                    trans.commit();
                }
            }
        } else {
            var player = source.getPlayer().orElseThrow(() -> new NoPlayerBlunder(trick));
            var inventory = player.getInventory();

            for (int i = 0; i < inventory.size(); i++) {
                var stack = inventory.getStack(i);

                if (validator.test(stack)) {
                    result = stack.copyWithCount(1);
                    stack.decrement(1);
                    break;
                }
            }
        }

        return Optional.ofNullable(result);
    }

    public SlotFragment findSlotOnPlayer(Trick<?> trick, Item type) {
        var player = source().getPlayer().orElseThrow(() -> new NoPlayerBlunder(trick));
        var inventory = player.getInventory();

        for (int i = 0; i < inventory.size(); i++) {
            var stack = inventory.getStack(i);

            if (stack.isOf(type)) {
                return new SlotFragment(new StorageSource.Slot(i, StorageSource.Caster.INSTANCE), VariantType.ITEM);
            }
        }

        throw new MissingItemBlunder(trick);
    }
}
