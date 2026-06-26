package dev.enjarai.trickster.datagen.provider;

import com.mojang.datafixers.util.Either;
import dev.enjarai.trickster.data.conversion.SimpleBlockConversionLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Maybe make this generated?
public class SimpleBlockConversionBuilder implements ThisIsABuilder<SimpleBlockConversionLoader.WeightedValue> {
    private final List<SimpleBlockConversionLoader.WeightedValue> entries = new ArrayList<>();

    public static SimpleBlockConversionBuilder create() {
        return new SimpleBlockConversionBuilder();
    }

    public List<SimpleBlockConversionLoader.WeightedValue> build() {
        return List.copyOf(this.entries);
    }

    public SimpleBlockConversionBuilder add(Block block, int weight) {
        add(block.getDefaultState(), weight);
        return this;
    }

    public SimpleBlockConversionBuilder add(BlockState state, int weight) {
        this.entries.add(new SimpleBlockConversionLoader.WeightedValue(state, Optional.empty(), Optional.empty(), weight));
        return this;
    }

    public SimpleBlockConversionBuilder add(Block block, List<String> keepProperties, int weight) {
        add(block.getDefaultState(), keepProperties, weight);
        return this;
    }

    public SimpleBlockConversionBuilder add(BlockState state, List<String> keepProperties, int weight) {
        this.entries.add(new SimpleBlockConversionLoader.WeightedValue(state, Optional.of(Either.left(keepProperties)), Optional.empty(), weight));
        return this;
    }

    public SimpleBlockConversionBuilder add(Block block, boolean keepProperties, int weight) {
        add(block.getDefaultState(), keepProperties, weight);
        return this;
    }

    public SimpleBlockConversionBuilder add(BlockState state, boolean keepProperties, int weight) {
        this.entries.add(new SimpleBlockConversionLoader.WeightedValue(state, Optional.of(Either.right(keepProperties)), Optional.empty(), weight));
        return this;
    }

    public SimpleBlockConversionBuilder add(Block block, NbtCompound nbt, int weight) {
        return add(block.getDefaultState(), nbt, weight);
    }

    public SimpleBlockConversionBuilder add(BlockState state, NbtCompound nbt, int weight) {
        this.entries.add(new SimpleBlockConversionLoader.WeightedValue(state, Optional.empty(), Optional.ofNullable(nbt), weight));
        return this;
    }

    public SimpleBlockConversionBuilder add(Block block, List<String> keepProperties, NbtCompound nbt, int weight) {
        return add(block.getDefaultState(), keepProperties, nbt, weight);
    }

    public SimpleBlockConversionBuilder add(BlockState state, List<String> keepProperties, NbtCompound nbt, int weight) {
        this.entries.add(new SimpleBlockConversionLoader.WeightedValue(state, Optional.of(Either.left(keepProperties)), Optional.ofNullable(nbt), weight));
        return this;
    }

    public SimpleBlockConversionBuilder add(Block block, boolean keepProperties, NbtCompound nbt, int weight) {
        return add(block.getDefaultState(), keepProperties, nbt, weight);
    }

    public SimpleBlockConversionBuilder add(BlockState state, boolean keepProperties, NbtCompound nbt, int weight) {
        this.entries.add(new SimpleBlockConversionLoader.WeightedValue(state, Optional.of(Either.right(keepProperties)), Optional.ofNullable(nbt), weight));
        return this;
    }
}
