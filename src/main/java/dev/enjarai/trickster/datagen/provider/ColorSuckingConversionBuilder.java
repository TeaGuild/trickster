package dev.enjarai.trickster.datagen.provider;

import com.mojang.datafixers.util.Either;
import dev.enjarai.trickster.data.conversion.ColorSuckingConversionLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ColorSuckingConversionBuilder implements ThisIsABuilder<ColorSuckingConversionLoader.ColoredValue> {
    private final List<ColorSuckingConversionLoader.ColoredValue> entries = new ArrayList<>();

    public static ColorSuckingConversionBuilder create() {
        return new ColorSuckingConversionBuilder();
    }

    public List<ColorSuckingConversionLoader.ColoredValue> build() {
        return List.copyOf(this.entries);
    }

    public ColorSuckingConversionBuilder add(Block block, int weight, int color, float manaAmount) {
        add(block.getDefaultState(), weight, color, manaAmount);
        return this;
    }

    public ColorSuckingConversionBuilder add(BlockState state, int weight, int color, float manaAmount) {
        this.entries.add(new ColorSuckingConversionLoader.ColoredValue(state, Optional.empty(), Optional.empty(), weight, color, manaAmount));
        return this;
    }

    public ColorSuckingConversionBuilder add(Block block, List<String> keepProperties, int weight, int color, float manaAmount) {
        add(block.getDefaultState(), keepProperties, weight, color, manaAmount);
        return this;
    }

    public ColorSuckingConversionBuilder add(BlockState state, List<String> keepProperties, int weight, int color, float manaAmount) {
        this.entries.add(new ColorSuckingConversionLoader.ColoredValue(state, Optional.of(Either.left(keepProperties)), Optional.empty(), weight, color, manaAmount));
        return this;
    }

    public ColorSuckingConversionBuilder add(Block block, boolean keepProperties, int weight, int color, float manaAmount) {
        add(block.getDefaultState(), keepProperties, weight, color, manaAmount);
        return this;
    }

    public ColorSuckingConversionBuilder add(BlockState state, boolean keepProperties, int weight, int color, float manaAmount) {
        this.entries.add(new ColorSuckingConversionLoader.ColoredValue(state, Optional.of(Either.right(keepProperties)), Optional.empty(), weight, color, manaAmount));
        return this;
    }

    public ColorSuckingConversionBuilder add(Block block, NbtCompound nbt, int weight, int color, float manaAmount) {
        return add(block.getDefaultState(), nbt, weight, color, manaAmount);
    }

    public ColorSuckingConversionBuilder add(BlockState state, NbtCompound nbt, int weight, int color, float manaAmount) {
        this.entries.add(new ColorSuckingConversionLoader.ColoredValue(state, Optional.empty(), Optional.ofNullable(nbt), weight, color, manaAmount));
        return this;
    }

    public ColorSuckingConversionBuilder add(Block block, List<String> keepProperties, NbtCompound nbt, int weight, int color, float manaAmount) {
        return add(block.getDefaultState(), keepProperties, nbt, weight, color, manaAmount);
    }

    public ColorSuckingConversionBuilder add(BlockState state, List<String> keepProperties, NbtCompound nbt, int weight, int color, float manaAmount) {
        this.entries.add(new ColorSuckingConversionLoader.ColoredValue(state, Optional.of(Either.left(keepProperties)), Optional.ofNullable(nbt), weight, color, manaAmount));
        return this;
    }

    public ColorSuckingConversionBuilder add(Block block, boolean keepProperties, NbtCompound nbt, int weight, int color, float manaAmount) {
        return add(block.getDefaultState(), keepProperties, nbt, weight, color, manaAmount);
    }

    public ColorSuckingConversionBuilder add(BlockState state, boolean keepProperties, NbtCompound nbt, int weight, int color, float manaAmount) {
        this.entries.add(new ColorSuckingConversionLoader.ColoredValue(state, Optional.of(Either.right(keepProperties)), Optional.ofNullable(nbt), weight, color, manaAmount));
        return this;
    }
}
