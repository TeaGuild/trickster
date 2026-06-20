package dev.enjarai.trickster.spell.trick.mana;

import java.util.List;
import java.util.function.Predicate;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.NumberIsInfiniteBlunder;
import dev.enjarai.trickster.spell.blunder.NumberTooSmallBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ManaFragment;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.slot.StorageFragment;
import dev.enjarai.trickster.spell.fragment.slot.VariantType;
import dev.enjarai.trickster.spell.mana.ManaPool;
import dev.enjarai.trickster.spell.mana.storage.ManaStorage;
import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import dev.enjarai.trickster.spell.mana.type.Mana;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.util.Meowing;
import dev.enjarai.trickster.util.StorageViewContainerContext;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public abstract class AbstractConduitTrick extends Trick<AbstractConduitTrick> {
    public AbstractConduitTrick(Pattern pattern) {
        super(pattern, Signature.of(FragmentType.NUMBER, ArgType.simple(StorageFragment.class), FragmentType.MANA.variadicOfArg().unpack(),
            AbstractConduitTrick::run, FragmentType.NUMBER));
    }

    public NumberFragment run(SpellContext ctx, NumberFragment n, StorageFragment holder, List<ManaFragment> wrappedFilter) {
        if (n.number() < 0) {
            throw new NumberTooSmallBlunder(this, 0);
        }

        if (Double.isInfinite(n.number())) {
            throw new NumberIsInfiniteBlunder(this);
        }

        var storage = holder.getStorage(this, ctx, VariantType.ITEM);

        if (!isThereEvenAnyPointToTryingToRunThisTrickRightNowQuestionMark(ctx)) {
            return new NumberFragment(0);
        }

        var filter = wrappedFilter.stream().map(ManaFragment::mana).toList();
        Predicate<Mana> meow = mana -> filter.isEmpty() || filter.contains(mana);

        long result;

        if (storage instanceof SlottedStorage<ItemVariant> slottedStorage) {
            result = runForSlottedStorage(ctx, slottedStorage, meow, n.asMana(), holder);
        } else {
            result = runForStorage(ctx, storage, meow, n.asMana(), holder);
        }

        return NumberFragment.ofMana(result);
    }

    private long runForSlottedStorage(SpellContext ctx, SlottedStorage<ItemVariant> storage, Predicate<Mana> filter, long max, StorageFragment holder) {
        long knotCount = 0;
        for (var slot : storage.getSlots()) {
            knotCount += ManaStorage.ITEM.find(
                slot.getResource().toStack(),
                new ManaStorage.LookupContext(ContainerItemContext.ofSingleSlot(slot), ctx.source().getWorld())
            ) == null ? 0 : 1;
        }

        if (knotCount == 0) return 0;

        long limit = max / knotCount;
        long result = 0;

        for (var slot : storage.getSlots()) {
            try (var trans = Transaction.openOuter()) {
                var manaStorage = ManaStorage.ITEM.find(
                    slot.getResource().toStack(),
                    new ManaStorage.LookupContext(ContainerItemContext.ofSingleSlot(slot), ctx.source().getWorld())
                );

                if (manaStorage == null) continue;

                result += affect(ctx, manaStorage, filter, limit / slot.getAmount(), trans, holder);

                trans.commit();
            }
        }

        return result;
    }

    private long runForStorage(SpellContext ctx, Storage<ItemVariant> storage, Predicate<Mana> filter, long max, StorageFragment holder) {
        long knotCount = 0;
        for (var view : storage.nonEmptyViews()) {
            knotCount += ManaStorage.ITEM.find(
                view.getResource().toStack(),
                new ManaStorage.LookupContext(new StorageViewContainerContext(view, storage), ctx.source().getWorld())
            ) == null ? 0 : 1;
        }

        if (knotCount == 0) return 0;

        long limit = max / knotCount;
        long result = 0;

        for (var view : storage.nonEmptyViews()) {
            try (var trans = Transaction.openOuter()) {
                var manaStorage = ManaStorage.ITEM.find(
                    view.getResource().toStack(),
                    new ManaStorage.LookupContext(new StorageViewContainerContext(view, storage), ctx.source().getWorld())
                );

                if (manaStorage == null) continue;

                result += affect(ctx, manaStorage, filter, limit / view.getAmount(), trans, holder);

                trans.commit();
            }
        }

        return result;
    }

    private long affect(SpellContext ctx, Storage<ManaVariant> storage, Predicate<Mana> filter, long limit, TransactionContext trans, StorageFragment holder) {
        var source = getSource(ctx, storage);
        var target = getTarget(ctx, storage);

        double taxedMult = getManaTransferTaxMultiplier(ctx, holder.getSourceOrCasterPos(this, ctx));

        long result = 0;
        long taken = 0;

        for (var view : source) {
            if (taken < limit) {
                var left = view.getResource();

                if (filter.test(left.getMana())) {
                    long extracted = source.extract(left, limit - taken, trans);
                    long inserted = insertMixing(ctx, target, left, (long) (extracted * taxedMult), trans);
                    long given = (long) (inserted / taxedMult);

                    taken += given;
                    result += inserted;

                    source.insert(left, extracted - given, trans);

                    if (given > 1) {
                        Meowing.addBubblingCallback(trans, transStatus -> {
                            if (transStatus.wasCommitted()) {
                                createParticles(ctx, holder, left, (float) given / ManaPool.MANA_SCALE);
                            }
                        });
                    }
                }
            }
        }

        return result;
    }

    protected abstract boolean isThereEvenAnyPointToTryingToRunThisTrickRightNowQuestionMark(SpellContext ctx);

    protected abstract Storage<ManaVariant> getSource(SpellContext ctx, Storage<ManaVariant> storage);

    protected abstract Storage<ManaVariant> getTarget(SpellContext ctx, Storage<ManaVariant> storage);

    protected abstract void createParticles(SpellContext ctx, StorageFragment holder, ManaVariant variant, float amount);
}
