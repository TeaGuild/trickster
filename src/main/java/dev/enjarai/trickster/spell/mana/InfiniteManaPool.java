package dev.enjarai.trickster.spell.mana;

import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import dev.enjarai.trickster.spell.mana.type.Manae;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import dev.enjarai.trickster.EndecTomfoolery;
import io.wispforest.endec.StructEndec;

public final class InfiniteManaPool implements MutableManaPool {
    public static final InfiniteManaPool INSTANCE = new InfiniteManaPool(ManaVariant.of(Manae.TRADITIONAL));
    public static final StructEndec<InfiniteManaPool> ENDEC = EndecTomfoolery.unit(INSTANCE);

    private final ManaVariant variant;

    private InfiniteManaPool(ManaVariant variant) {
        this.variant = variant;
    }

    @Override
    @Nullable
    public ManaPoolType<?> type() {
        return ManaPoolType.INFINITE;
    }

    @Override
    public ManaVariant getVariant(World world) {
        return variant;
    }

    @Override
    public long get(World world) {
        return Long.MAX_VALUE / 3;
    }

    @Override
    public long getMax(World world) {
        return Long.MAX_VALUE / 3;
    }

    @Override
    public void setVariant(ManaVariant variant, World world) {}

    @Override
    public void set(long current, World world) {}

    @Override
    public void setMax(long max, World world) {}

    @Override
    public MutableManaPool makeClone(World world) throws UnsupportedOperationException {
        if (variant == INSTANCE.variant) {
            return INSTANCE;
        }

        return new InfiniteManaPool(variant);
    }
}
