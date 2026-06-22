package dev.enjarai.trickster.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.item.KnotItem;
import dev.enjarai.trickster.spell.mana.ManaPool;
import dev.enjarai.trickster.spell.mana.SimpleManaPool;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public record ManaComponent(ManaPool pool, float naturalRechargeMultiplier) {

    public static final ManaComponent EMPTY = new ManaComponent(SimpleManaPool.getSingleUse(0), 0);
    public static final Codec<ManaComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        EndecTomfoolery.toCodec(ManaPool.ENDEC).fieldOf("pool").forGetter(ManaComponent::pool),
        Codec.FLOAT.fieldOf("natural_recharge_multiplier").forGetter(ManaComponent::naturalRechargeMultiplier)
    ).apply(instance, ManaComponent::new));

    public ManaComponent(ManaPool pool) {
        this(pool, 0);
    }

    public ManaComponent with(ManaPool pool) {
        return new ManaComponent(pool, naturalRechargeMultiplier());
    }

    public static float tryRecharge(World world, Vec3d pos, ItemStack stack) {
        if (stack.getItem() instanceof KnotItem knot) {
            return knot.tryRecharge(world, pos, stack);
        }
        return 0;
    }
}
