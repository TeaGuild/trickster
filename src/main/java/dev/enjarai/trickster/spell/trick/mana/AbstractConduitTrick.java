package dev.enjarai.trickster.spell.trick.mana;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.NumberIsInfiniteBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.slot.StorageFragment;
import dev.enjarai.trickster.spell.fragment.slot.VariantType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.Signature;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.item.ItemStack;

public abstract class AbstractConduitTrick extends Trick<AbstractConduitTrick> {
    public AbstractConduitTrick(Pattern pattern) {
        super(pattern, Signature.of(FragmentType.NUMBER, ArgType.simple(StorageFragment.class), AbstractConduitTrick::run, FragmentType.NUMBER));
    }

    public NumberFragment run(SpellContext ctx, NumberFragment n, StorageFragment target) {
        if (Double.isInfinite(n.number())) {
            throw new NumberIsInfiniteBlunder(this);
        }

        var storage = target.getStorage(this, ctx, VariantType.ITEM);

        if (!isThereEvenAnyPointToTryingToRunThisTrickRightNowQuestionMark(ctx)) {
            return new NumberFragment(0);
        }

        float result;

        if (storage instanceof SlottedStorage<ItemVariant> slottedStorage) {
            result = runForSlottedStorage(ctx, slottedStorage, target, n.number());
        } else {
            result = runForStorage(ctx, storage, target, n.number());
        }

        return new NumberFragment(result);
    }

    private float runForSlottedStorage(SpellContext ctx, SlottedStorage<ItemVariant> storage, StorageFragment target, double max) {
        float result = 0;

        long knotCount = 0;
        for (var slot : storage.getSlots()) {
            if (slot.getResource().getComponentMap().contains(ModComponents.MANA)) {
                knotCount += slot.getAmount();
            }
        }

        double limit = max / knotCount;

        var distance = ctx.source().getPos().distance(target.getSourceOrCasterPos(this, ctx));
        float r = Trickster.CONFIG.manaTransferEfficiency();
        double tax = Math.max(0, 1 - r / (distance + r - 16));

        for (var slot : storage.getSlots()) {
            var resource = slot.getResource();
            if (resource.isBlank()) {
                continue;
            }

            try (var trans2 = Transaction.openOuter()) {
                var total = slot.getAmount();
                var taken = slot.extract(resource, Long.MAX_VALUE, trans2);

                if (total != taken) {
                    continue;
                }

                var tempStack = resource.toStack(1);
                result += affect(ctx, tempStack, (float) limit, tax, taken);

                var newResource = ItemVariant.of(tempStack);
                if (resource.equals(newResource)) {
                    continue;
                }

                var given = slot.insert(newResource, taken, trans2);

                if (given != taken) {
                    continue;
                }
                trans2.commit();
            }
        }

        if (result > 1) {
            createParticles(ctx, target, result);
        }

        return result;
    }

    private float runForStorage(SpellContext ctx, Storage<ItemVariant> storage, StorageFragment target, double max) {
        float result = 0;

        long knotCount = 0;
        for (var view : storage.nonEmptyViews()) {
            if (view.getResource().getComponentMap().contains(ModComponents.MANA)) {
                knotCount += view.getAmount();
            }
        }

        double limit = max / knotCount;

        var distance = ctx.source().getPos().distance(target.getSourceOrCasterPos(this, ctx));
        float r = Trickster.CONFIG.manaTransferEfficiency();
        double tax = Math.max(0, 1 - r / (distance + r - 16));

        for (var view : storage.nonEmptyViews()) {
            var resource = view.getResource();

            try (var trans2 = Transaction.openOuter()) {
                var total = view.getAmount();
                var taken = view.extract(resource, Long.MAX_VALUE, trans2);

                if (total != taken) {
                    continue;
                }

                var tempStack = resource.toStack(1);
                result += affect(ctx, tempStack, (float) limit, tax, taken);

                var newResource = ItemVariant.of(tempStack);
                if (resource.equals(newResource)) {
                    continue;
                }

                var given = storage.insert(newResource, taken, trans2);

                if (given != taken) {
                    continue;
                }
                trans2.commit();
            }
        }

        if (result > 1) {
            createParticles(ctx, target, result);
        }

        return result;
    }

    protected abstract boolean isThereEvenAnyPointToTryingToRunThisTrickRightNowQuestionMark(SpellContext ctx);

    protected abstract void createParticles(SpellContext ctx, StorageFragment slot, float v);

    /**
     * @return the amount of mana fulfilled by the item.
     */
    protected abstract float affect(SpellContext ctx, ItemStack stack, float limit, double taxPercentage, long count);
}
