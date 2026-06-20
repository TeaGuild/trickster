package dev.enjarai.trickster.spell.mana;

import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import dev.enjarai.trickster.spell.mana.type.Manae;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.world.World;

public class OverfillingManaPool extends SimpleManaPool {
    public static final StructEndec<OverfillingManaPool> ENDEC = StructEndecBuilder.of(
        LONG_OR_OLD_FLOAT_ENDEC.fieldOf("mana", overfillingManaPool -> overfillingManaPool.mana),
        LONG_OR_OLD_FLOAT_ENDEC.fieldOf("max_mana", overfillingManaPool -> overfillingManaPool.maxMana),
        ManaVariant.ENDEC.optionalFieldOf("variant", overfillingManaPool -> overfillingManaPool.variant, ManaVariant.of(Manae.TRADITIONAL)),
        OverfillingManaPool::new
    );

    public OverfillingManaPool(long mana, long maxMana, ManaVariant variant) {
        super(mana, maxMana, variant);
    }

    public OverfillingManaPool(long maxMana) {
        super(maxMana);
    }

    @Override
    public ManaPoolType<?> type() {
        return ManaPoolType.OVERFILLING;
    }

    @Override
    public void set(long value, World world) {
        mana = value;
    }

    @Override
    public MutableManaPool makeClone(World world) {
        return new OverfillingManaPool(mana, maxMana, variant);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof OverfillingManaPool && super.equals(obj);
    }
}
