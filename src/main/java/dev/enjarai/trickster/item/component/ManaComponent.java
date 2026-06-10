package dev.enjarai.trickster.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.spell.mana.ManaPool;
import dev.enjarai.trickster.spell.mana.SimpleManaPool;
import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import dev.enjarai.trickster.spell.mana.type.Manae;
import dev.enjarai.trickster.spell.mana.type.component.ManaComponents;
import dev.enjarai.trickster.spell.mana.type.component.StabilityComponent;
import net.minecraft.component.ComponentChanges;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
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
        if (world.isDay()) {
            return 0;
        }

        if (world.isRaining() && !world.isThundering()) {
            return 0;
        }

        var component = stack.get(ModComponents.MANA);
        if (component == null || component.naturalRechargeMultiplier() <= 0) {
            return 0;
        }

        var pool = component.pool();
        if (pool.get(world) >= pool.getMax(world) && !world.isThundering()) {
            return 0;
        }

        // Require skylight access
        var noSky = world.getLightLevel(LightType.SKY, BlockPos.ofFloored(pos)) < 15;
        if (noSky) {
            return 0;
        }

        var moonSize = world.getMoonSize();
        // Recharge most strongly at new and full moons, with no recharging halfway inbetween
        // 20x and storm mana when thundering
        var chargeMultiplier = world.isThundering() ? 25 : Math.max(Math.max(moonSize * 2 - 1, 1 - moonSize * 2), 0);
        chargeMultiplier *= component.naturalRechargeMultiplier();

        var mana = world.isThundering() ? Manae.STORM : Manae.TRADITIONAL;
        var newPool = pool.makeClone(world);

        var variant = ManaVariant.of(mana);
        if (mana == Manae.STORM) {
            variant = variant.withComponentChanges(ComponentChanges.builder()
                .add(ManaComponents.STABILITY, new StabilityComponent(world.getTime())).build());

            if (newPool.getVariant(world).getMana() == Manae.STORM) {
                newPool.setVariant(variant, world);
            }
        }

        var shouldMakeParticles = 0 == newPool.refill(variant, (long) (chargeMultiplier * ManaPool.MANA_SCALE), world);
        stack.set(ModComponents.MANA, component.with(newPool));

        if (shouldMakeParticles) {
            var particleVelocity = Math.ceil(chargeMultiplier / 2f);
            var horizontalMultiplier = world.isThundering() ? 6 : 1;

            var recharged = chargeMultiplier - world.random.nextFloat();
            while (recharged > 0) {
                world.addParticle(
                    mana.getParticle(),
                    pos.getX(), pos.getY(), pos.getZ(),
                    (world.random.nextFloat() * 0.005f - 0.0025f) * particleVelocity * horizontalMultiplier,
                    (world.random.nextFloat() * 0.02f + 0.01f) * particleVelocity,
                    (world.random.nextFloat() * 0.005f - 0.0025f) * particleVelocity * horizontalMultiplier
                );
                recharged -= world.random.nextFloat();
            }
        }

        return chargeMultiplier;
    }
}
