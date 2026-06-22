package dev.enjarai.trickster.spell.mana;

import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import dev.enjarai.trickster.spell.mana.type.Manae;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.world.World;

public class SimpleManaPool implements MutableManaPool {
    public static final StructEndec<SimpleManaPool> ENDEC = StructEndecBuilder.of(
        LONG_OR_OLD_FLOAT_ENDEC.fieldOf("mana", simpleManaPool -> simpleManaPool.mana),
        LONG_OR_OLD_FLOAT_ENDEC.fieldOf("max_mana", simpleManaPool -> simpleManaPool.maxMana),
        ManaVariant.ENDEC.optionalFieldOf("variant", simpleManaPool -> simpleManaPool.variant, ManaVariant.of(Manae.TRADITIONAL)),
        SimpleManaPool::new
    );

    protected long maxMana;
    protected long mana;
    protected ManaVariant variant = ManaVariant.EMPTY;

    public SimpleManaPool(long maxMana) {
        this.maxMana = maxMana;
    }

    public SimpleManaPool(long mana, long maxMana, ManaVariant variant) {
        this.mana = mana;
        this.maxMana = maxMana;
        this.variant = variant;
    }

    @Override
    public ManaPoolType<?> type() {
        return ManaPoolType.SIMPLE;
    }

    @Override
    public ManaVariant getVariant(World world) {
        return get(world) > 0 ? variant : ManaVariant.EMPTY;
    }

    @Override
    public void setVariant(ManaVariant variant, World world) {
        this.variant = variant;
    }

    @Override
    public void set(long value, World world) {
        mana = Math.clamp(value, 0, maxMana);
    }

    @Override
    public long get(World world) {
        return mana;
    }

    @Override
    public void setMax(long value, World world) {
        maxMana = value;
    }

    @Override
    public long getMax(World world) {
        return maxMana;
    }

    @Override
    public MutableManaPool makeClone(World world) {
        return new SimpleManaPool(mana, maxMana, variant);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SimpleManaPool pool
            && (pool.variant == this.variant || this.mana == 0)
            && pool.maxMana == this.maxMana
            && pool.mana == this.mana;
    }

    @Override
    public int hashCode() {
        return (int) (maxMana + mana);
    }

    public static SimpleManaPool getSingleUse(long mana) {
        return new SimpleManaPool(mana, mana, ManaVariant.of(Manae.TRADITIONAL));
    }
}
