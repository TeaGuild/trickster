package dev.enjarai.trickster.spell.fragment.slot;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ZalgoFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.text.Text;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3dc;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public record ContainerFragment(StorageSource source, VariantType<?> variantType, Optional<List<ResourceVariantFragment>> filter) implements StorageFragment {

    public static final StructEndec<ContainerFragment> ENDEC = StructEndecBuilder.of(
        StorageSource.ENDEC.fieldOf("source", ContainerFragment::source),
        VariantType.ENDEC.fieldOf("variant_type", ContainerFragment::variantType),
        EndecTomfoolery.protocolOptional((byte) 5, Fragment.ENDEC.xmap(f -> (ResourceVariantFragment) f, rvf -> rvf).listOf().optionalOf(), () -> Optional.empty()).fieldOf("filter",
            ContainerFragment::filter),
        ContainerFragment::new
    );

    public ContainerFragment filtered(List<ResourceVariantFragment> filter) {
        return this.filter
            .map(currentFilter -> new ContainerFragment(
                source,
                variantType,
                Optional.of(currentFilter.stream().distinct().filter(filter::contains).toList())
            ))
            .orElseGet(() -> new ContainerFragment(
                source,
                variantType,
                Optional.of(filter.stream().filter(rvf -> rvf.variantType().equals(variantType)).toList())
            ));
    }

    @Override
    public FragmentType<?> type() {
        return FragmentType.CONTAINER;
    }

    @Override
    public Text asText() {
        return source.describe();
    }

    @Override
    public int getWeight() {
        return 32 + source.getWeight();
    }

    @Override
    public Fragment applyEphemeral() {
        if (source.isEphemeral()) {
            return new ZalgoFragment();
        }
        return this;
    }

    public Storage<?> getStorage(Trick<?> trick, SpellContext ctx) {
        return applyFilter(source.getStorage(trick, ctx, variantType));
    }

    public <T> Storage<T> getStorage(Trick<?> trick, SpellContext ctx, VariantType<T> expectedVariantType) {
        assertVariantType(trick, expectedVariantType);
        return applyFilter(source.getStorage(trick, ctx, expectedVariantType));
    }

    @Override
    public Optional<Vector3dc> getSourcePos(Trick<?> trick, SpellContext ctx) {
        if (source() == StorageSource.Caster.INSTANCE) {
            return Optional.empty();
        } else {
            return Optional.of(source().getPosition(trick, ctx));
        }
    }

    @Override
    public Vector3dc getSourceOrCasterPos(Trick<?> trick, SpellContext ctx) {
        return source().getPosition(trick, ctx);
    }

    private <T> Storage<T> applyFilter(Storage<T> storage) {
        return filter.<Storage<T>>map(filter -> new Storage<T>() {
            @Override
            public long insert(T resource, long maxAmount, TransactionContext transaction) {
                if (filter.stream().noneMatch(rvf -> rvf.resourceMatches(resource))) return 0;
                return storage.insert(resource, maxAmount, transaction);
            }

            @Override
            public long extract(T resource, long maxAmount, TransactionContext transaction) {
                if (filter.stream().noneMatch(rvf -> rvf.resourceMatches(resource))) return 0;
                return storage.extract(resource, maxAmount, transaction);
            }

            @Override
            public Iterator<StorageView<T>> iterator() {
                var inner = storage.iterator();

                return new Iterator<>() {
                    private @Nullable StorageView<T> next;

                    @Override
                    public boolean hasNext() {
                        return findNext() != null;
                    }

                    @Override
                    public StorageView<T> next() {
                        var next = findNext();
                        this.next = null;
                        return next;
                    }

                    private @Nullable StorageView<T> findNext() {
                        if (next == null) {
                            while (inner.hasNext()) {
                                var next = inner.next();

                                if (filter.stream().anyMatch(rvf -> rvf.resourceMatches(next.getResource()))) {
                                    this.next = next;
                                }
                            }
                        }

                        return next;
                    }
                };
            }
        }).orElse(storage);
    }
}
