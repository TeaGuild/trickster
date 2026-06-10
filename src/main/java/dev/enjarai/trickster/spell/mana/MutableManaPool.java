package dev.enjarai.trickster.spell.mana;

import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import io.wispforest.endec.Endec;
import net.minecraft.world.World;

public interface MutableManaPool extends ManaPool {
    Endec<MutableManaPool> ENDEC = ManaPool.ENDEC.xmap(imp -> (MutableManaPool) imp, mp -> mp);

    void setVariant(ManaVariant variant, World world);

    void set(long current, World world);

    void setMax(long max, World world);

    /**
     * @param amount the amount to consume from this pool.
     * @param world
     * @return the amount not consumed from this pool.
     */
    default long use(ManaVariant variant, long amount, World world) {
        if (!variant.equals(getVariant(world))) {
            return amount;
        } else if (get(world) >= amount) {
            set(get(world) - amount, world);
            return 0;
        } else {
            var result = amount - get(world);
            set(0, world);
            setVariant(ManaVariant.EMPTY, world);
            return result;
        }
    }

    /**
     * @param amount the amount to refill this pool with.
     * @param world
     * @return the amount that could not be added to this pool.
     */
    default long refill(ManaVariant variant, long amount, World world) {
        if (!getVariant(world).isBlank() && !variant.equals(getVariant(world))) {
            return amount;
        } else if (getMax(world) - get(world) >= amount) {
            set(get(world) + amount, world);
            setVariant(variant, world);
            return 0;
        } else {
            var result = amount - (getMax(world) - get(world));
            set(getMax(world), world);
            if (result != amount) {
                setVariant(variant, world);
            }
            return result;
        }
    }
}
