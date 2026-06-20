package dev.enjarai.trickster.data.conversion;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.enjarai.trickster.mixin.accessor.StateAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;

import java.util.List;
import java.util.Optional;

public class ColorSuckingConversionLoader extends BlockConversionLoader<ColorSuckingConversionLoader.ColoredValue> {
    public ColorSuckingConversionLoader(RegistryWrapper.WrapperLookup registryLookup) {
        super("color_sucking", ColoredValue.CODEC, registryLookup);
    }

    public record ColoredValue(BlockState state, Optional<Either<List<String>, Boolean>> keepProperties,
        Optional<NbtCompound> nbt, int weight, int color, float manaAmount) implements Conversion {

        public static final MapCodec<BlockState> BLOCK_STATE_CODEC = Registries.BLOCK.getCodec().dispatchMap("id", state -> ((StateAccessor) state).getOwner(), owner -> {
            BlockState state = owner.getDefaultState();
            if (state.getEntries().isEmpty()) {
                return MapCodec.unit(state);
            }
            return ((StateAccessor) state).<BlockState>getCodec().codec().optionalFieldOf("properties").xmap(optional -> optional.orElse(state), Optional::of);
        }).stable();

        public static final Codec<ColoredValue> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RecordCodecBuilder.of(ColoredValue::state, BLOCK_STATE_CODEC),
            Codec.either(Codec.STRING.listOf(), Codec.BOOL).optionalFieldOf("keepProperties").forGetter(ColoredValue::keepProperties),
            NbtCompound.CODEC.optionalFieldOf("nbt").forGetter(ColoredValue::nbt),
            Codec.INT.fieldOf("weight").forGetter(ColoredValue::weight),
            Codec.INT.fieldOf("color").forGetter(ColoredValue::color),
            Codec.FLOAT.fieldOf("mana_amount").forGetter(ColoredValue::manaAmount)
        ).apply(instance, ColoredValue::new)
        );

        @Override
        public double getWeight() {
            return weight;
        }
    }
}
