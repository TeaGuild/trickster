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

public abstract class SimpleBlockConversionLoader extends BlockConversionLoader<SimpleBlockConversionLoader.WeightedValue> {
    public SimpleBlockConversionLoader(String dataType, RegistryWrapper.WrapperLookup registryLookup) {
        super(dataType, WeightedValue.CODEC, registryLookup);
    }

    public record WeightedValue(BlockState state, Optional<Either<List<String>, Boolean>> keepProperties,
        Optional<NbtCompound> nbt, int weight) implements Conversion {

        public static final MapCodec<BlockState> BLOCK_STATE_CODEC = Registries.BLOCK.getCodec().dispatchMap("id", state -> ((StateAccessor) state).getOwner(), owner -> {
            BlockState state = owner.getDefaultState();
            if (state.getEntries().isEmpty()) {
                return MapCodec.unit(state);
            }
            return ((StateAccessor) state).<BlockState>getCodec().codec().optionalFieldOf("properties").xmap(optional -> optional.orElse(state), Optional::of);
        }).stable();

        public static final Codec<WeightedValue> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RecordCodecBuilder.of(WeightedValue::state, BLOCK_STATE_CODEC),
            Codec.either(Codec.STRING.listOf(), Codec.BOOL).optionalFieldOf("keepProperties").forGetter(WeightedValue::keepProperties),
            NbtCompound.CODEC.optionalFieldOf("nbt").forGetter(WeightedValue::nbt),
            Codec.INT.fieldOf("weight").forGetter(WeightedValue::weight)
        ).apply(instance, WeightedValue::new)
        );

        @Override
        public double getWeight() {
            return weight;
        }
    }
}
