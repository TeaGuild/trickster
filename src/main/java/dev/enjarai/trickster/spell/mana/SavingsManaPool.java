package dev.enjarai.trickster.spell.mana;

import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import dev.enjarai.trickster.spell.mana.type.Manae;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.world.World;

public class SavingsManaPool extends SimpleManaPool {
    public static final StructEndec<SavingsManaPool> ENDEC = StructEndecBuilder.of(
        LONG_OR_OLD_FLOAT_ENDEC.fieldOf("mana", pool -> pool.mana),
        LONG_OR_OLD_FLOAT_ENDEC.fieldOf("max_mana", pool -> pool.maxMana),
        ManaVariant.ENDEC.optionalFieldOf("variant", pool -> pool.variant, ManaVariant.of(Manae.TRADITIONAL)),
        Endec.FLOAT.fieldOf("interest", pool -> pool.interest),
        Endec.LONG.fieldOf("last_update_time", pool -> pool.lastUpdateTime),
        SavingsManaPool::new
    );

    protected final float interest;
    protected long lastUpdateTime;

    protected SavingsManaPool(long mana, long maxMana, ManaVariant variant, float interest, long lastUpdateTime) {
        super(mana, maxMana, variant);
        this.interest = interest;
        this.lastUpdateTime = lastUpdateTime;
    }

    // It's kinda important for this to be the only public constructor.
    // If lastUpdateTime is 0, mana should probably also be 0, or a lot of free interest will be given.
    public SavingsManaPool(long maxMana, float interest) {
        super(maxMana);
        this.interest = interest;
    }

    @Override
    public ManaPoolType<?> type() {
        return ManaPoolType.SAVINGS;
    }

    @Override
    public void set(long value, World world) {
        lastUpdateTime = world.getTime();
        super.set(value, world);
    }

    @Override
    public long get(World world) {
        if (!variant.isOf(Manae.TRADITIONAL) && interest > 0) return super.get(world);

        var ticksPassed = world.getTime() - lastUpdateTime; // t
        var lastMana = super.get(world); // P

        // P(1 + i)^t
        return (long) Math.clamp(lastMana * Math.pow(1 + interest, ticksPassed), 0, getMax(world));
    }

    @Override
    public MutableManaPool makeClone(World world) {
        return new SavingsManaPool(mana, maxMana, variant, interest, lastUpdateTime);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SavingsManaPool pool
            && pool.interest == this.interest
            && pool.lastUpdateTime == this.lastUpdateTime
            && super.equals(pool);
    }
}
