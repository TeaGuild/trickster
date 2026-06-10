package dev.enjarai.trickster.spell.trick.mana;

import java.util.Optional;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.NumberIsInfiniteBlunder;
import dev.enjarai.trickster.spell.blunder.NumberTooSmallBlunder;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ManaFragment;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.slot.StorageFragment;
import dev.enjarai.trickster.spell.fragment.slot.VariantType;
import dev.enjarai.trickster.spell.mana.ManaPool;
import dev.enjarai.trickster.spell.mana.storage.ManaStorage;
import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import dev.enjarai.trickster.spell.mana.type.Manae;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.util.StorageViewContainerContext;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public abstract class AbstractConduitTrick extends Trick<AbstractConduitTrick> {
    public AbstractConduitTrick(Pattern pattern) {
        super(pattern, Signature.of(FragmentType.NUMBER, ArgType.simple(StorageFragment.class), FragmentType.MANA.optionalOfArg(), FragmentType.BOOLEAN.optionalOfArg(), AbstractConduitTrick::run,
            FragmentType.NUMBER));
    }

    public NumberFragment run(SpellContext ctx, NumberFragment n, StorageFragment target, Optional<ManaFragment> mana, Optional<BooleanFragment> overrideConvert) {
        if (n.number() < 0) {
            throw new NumberTooSmallBlunder(this, 0);
        }

        if (Double.isInfinite(n.number())) {
            throw new NumberIsInfiniteBlunder(this);
        }

        var storage = target.getStorage(this, ctx, VariantType.ITEM);
        var variant = ManaVariant.of(mana.map(ManaFragment::mana).orElse(Manae.TRADITIONAL));
        boolean convert = overrideConvert.map(BooleanFragment::asBoolean).orElse(false);

        if (!isThereEvenAnyPointToTryingToRunThisTrickRightNowQuestionMark(ctx, variant, convert)) {
            return new NumberFragment(0);
        }

        long result;

        if (storage instanceof SlottedStorage<ItemVariant> slottedStorage) {
            result = runForSlottedStorage(ctx, slottedStorage, target, variant, n.asMana(), convert);
        } else {
            result = runForStorage(ctx, storage, target, variant, n.asMana(), convert);
        }

        return NumberFragment.ofMana(result);
    }

    private long runForSlottedStorage(SpellContext ctx, SlottedStorage<ItemVariant> storage, StorageFragment target, ManaVariant variant, long max, boolean convert) {
        long knotCount = 0;
        for (var slot : storage.getSlots()) {
            knotCount += ManaStorage.ITEM.find(
                slot.getResource().toStack(),
                new ManaStorage.LookupContext(ContainerItemContext.ofSingleSlot(slot), ctx.source().getWorld())
            ) == null ? 0 : 1;
        }

        if (knotCount == 0) return 0;

        long limit = max / knotCount;

        var distance = ctx.source().getPos().distance(target.getSourceOrCasterPos(this, ctx));
        float r = Trickster.CONFIG.manaTransferEfficiency();
        double tax = Math.max(0, 1 - r / (distance + r - 16));

        long result = 0;
        for (var slot : storage.getSlots()) {
            try (var trans = Transaction.openOuter()) {
                var manaStorage = ManaStorage.ITEM.find(
                    slot.getResource().toStack(),
                    new ManaStorage.LookupContext(ContainerItemContext.ofSingleSlot(slot), ctx.source().getWorld())
                );

                if (manaStorage == null) continue;

                result += affect(ctx, manaStorage, variant, limit / slot.getAmount(), tax, convert, trans);

                trans.commit();
            }
        }

        if (result > 1) {
            createParticles(ctx, target, (float) result / ManaPool.MANA_SCALE);
        }

        return result;
    }

    private long runForStorage(SpellContext ctx, Storage<ItemVariant> storage, StorageFragment target, ManaVariant variant, long max, boolean convert) {
        long knotCount = 0;
        for (var view : storage.nonEmptyViews()) {
            knotCount += ManaStorage.ITEM.find(
                view.getResource().toStack(),
                new ManaStorage.LookupContext(new StorageViewContainerContext(view, storage), ctx.source().getWorld())
            ) == null ? 0 : 1;
        }

        if (knotCount == 0) return 0;

        long limit = max / knotCount;

        var distance = ctx.source().getPos().distance(target.getSourceOrCasterPos(this, ctx));
        float r = Trickster.CONFIG.manaTransferEfficiency();
        double tax = Math.max(0, 1 - r / (distance + r - 16));

        long result = 0;
        for (var view : storage.nonEmptyViews()) {
            try (var trans = Transaction.openOuter()) {
                var manaStorage = ManaStorage.ITEM.find(
                    view.getResource().toStack(),
                    new ManaStorage.LookupContext(new StorageViewContainerContext(view, storage), ctx.source().getWorld())
                );

                if (manaStorage == null) continue;

                result += affect(ctx, manaStorage, variant, limit / view.getAmount(), tax, convert, trans);

                trans.commit();
            }
        }

        if (result > 1) {
            createParticles(ctx, target, result);
        }

        return result;
    }

    protected abstract boolean isThereEvenAnyPointToTryingToRunThisTrickRightNowQuestionMark(SpellContext ctx, ManaVariant variant, boolean convert);

    protected abstract void createParticles(SpellContext ctx, StorageFragment slot, float v);

    /**
     * @return the amount of mana fulfilled by the item.
     */
    protected abstract long affect(SpellContext ctx, Storage<ManaVariant> manaStorage, ManaVariant variant, long limit, double taxPercentage, boolean convert, TransactionContext trans);
}
