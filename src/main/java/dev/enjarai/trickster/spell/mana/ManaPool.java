package dev.enjarai.trickster.spell.mana;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface ManaPool {
    long MANA_SCALE = 1000;
    Endec<Long> LONG_OR_OLD_FLOAT_ENDEC = EndecTomfoolery.withAlternative(Endec.LONG,
        Endec.FLOAT.xmap(f -> (long) (f * MANA_SCALE), l -> (float) l / MANA_SCALE));
    @SuppressWarnings("unchecked")
    StructEndec<ManaPool> ENDEC = EndecTomfoolery.lazyStruct(() -> (StructEndec<ManaPool>) Endec.dispatchedStruct(ManaPoolType::endec, pool -> {
        var type = pool.type();

        if (type == null) {
            throw new UnsupportedOperationException("This mana pool type cannot be serialized");
        }

        return type;
    }, MinecraftEndecs.ofRegistry(ManaPoolType.REGISTRY)));

    @Nullable
    ManaPoolType<?> type();

    ManaVariant getVariant(World world);

    long get(World world);

    long getMax(World world);

    default boolean isFull(World world) {
        return get(world) >= getMax(world);
    }

    default boolean isEmpty(World world) {
        return get(world) <= 0;
    }

    MutableManaPool makeClone(World world) throws UnsupportedOperationException;
}
