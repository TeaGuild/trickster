package dev.enjarai.trickster.data.conversion;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.advancement.criterion.ModCriteria;
import dev.enjarai.trickster.data.CompleteJsonDataLoader;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;
import nl.enjarai.cicada.api.util.random.RandomUtil;

import java.util.*;
import java.util.function.Function;

public abstract class BlockConversionLoader<T extends Conversion> extends CompleteJsonDataLoader implements IdentifiableResourceReloadListener {
    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final Codec<Replaceable<T>> entryCodec;
    protected final RegistryWrapper.WrapperLookup registryLookup;
    private final String type;
    protected ImmutableMap<Block, List<T>> conversions = ImmutableMap.of();

    public BlockConversionLoader(String dataType, Codec<T> entryCodec, RegistryWrapper.WrapperLookup registryLookup) {
        // This uses CODEC in a future version
        // The inline commented code is for easier porting for future versions, as mojang changed this to use codecs
        super(GSON/*Replaceable.CODEC*/, "conversion/" + dataType);
        this.entryCodec = Replaceable.codec(entryCodec);
        this.registryLookup = registryLookup;
        this.type = dataType;
    }

    @Override
    public Identifier getFabricId() {
        return Trickster.id("conversion", type);
    }

    @Override
    // The inline commented code is for easier porting for future versions, as mojang changed this to use codecs
    protected void apply(Map<Identifier, List<JsonElement/*Replaceable*/>> prepared, ResourceManager manager, Profiler profiler) {
        RegistryWrapper.Impl<Block> lookup = registryLookup.getWrapperOrThrow(RegistryKeys.BLOCK);
        Map<Block, List<T>> map = new HashMap<>();

        prepared.forEach((identifier, jsonElements) -> {
            Block source = lookup.getOrThrow(RegistryKey.of(RegistryKeys.BLOCK, identifier)).value();
            map.compute(source, (block, weightedValues) -> {
                List<T> values = Objects.requireNonNullElseGet(weightedValues, ArrayList::new);

                for (JsonElement jsonElement : jsonElements) {
                    Replaceable<T> replaceable = entryCodec.parse(JsonOps.INSTANCE, jsonElement).getOrThrow();
                    if (replaceable.replace) {
                        values.clear();
                    }

                    values.addAll(replaceable.conversions);
                }

                return values;
            });
        });

        ImmutableMap.Builder<Block, List<T>> builder = ImmutableMap.builder();
        map.forEach((block, weightedValues) -> builder.put(block, List.copyOf(weightedValues)));

        conversions = builder.build();
    }

    public T convert(Block block, World world, BlockPos pos, Optional<ServerPlayerEntity> player) {
        List<T> values = conversions.get(block);
        if (values == null) return null;

        Optional<T> perhapsWeightedValue = RandomUtil.chooseWeighted(values);
        if (perhapsWeightedValue.isEmpty()) return null;

        T weightedValue = perhapsWeightedValue.get();

        BlockEntity blockEntity;
        BlockState blockState = Block.postProcessState(weightedValue.state(), world, pos);

        if (blockState.isAir()) {
            world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, pos, Block.getRawIdFromState(blockState));
        }

        if (weightedValue.keepProperties().isPresent()) {
            Either<List<String>, Boolean> either = weightedValue.keepProperties().get();
            Function<String, Boolean> checker;
            if (either.right().isPresent()) {
                boolean shouldKeepAll = either.right().get();
                checker = (name) -> shouldKeepAll;
            } else {
                List<String> toKeep = either.left().get();
                checker = toKeep::contains;
            }

            BlockState oldState = world.getBlockState(pos);
            for (Property<?> property : blockState.getEntries().keySet()) {
                if (checker.apply(property.getName()) && oldState.contains(property)) {
                    blockState = propertyFromOldBlock(blockState, oldState, property);
                }
            }
        }

        if (blockState.getFluidState().isOf(Fluids.WATER) && world.getDimension().ultrawarm()) {
            if (blockState.isOf(Blocks.WATER)) {
                blockState = Blocks.AIR.getDefaultState();
            } else {
                Optional<Boolean> perhapsWaterlogged = blockState.getOrEmpty(Properties.WATERLOGGED);
                if (perhapsWaterlogged.isPresent() && perhapsWaterlogged.get()) {
                    blockState = blockState.with(Properties.WATERLOGGED, false);
                }
            }
        }

        if (!world.setBlockState(pos, blockState)) {
            return null;
        }

        world.emitGameEvent(null, GameEvent.BLOCK_CHANGE, pos);

        if (weightedValue.nbt().isPresent() && (blockEntity = world.getBlockEntity(pos)) != null) {
            blockEntity.read(weightedValue.nbt().get(), world.getRegistryManager());
        }

        if (player.isPresent()) {
            ModCriteria.BLOCK_CONVERSION.trigger(player.get(), block, blockState.getBlock());
        }

        return weightedValue;
    }

    protected <T extends Comparable<T>> BlockState propertyFromOldBlock(BlockState newState, BlockState oldState, Property<T> property) {
        return newState.with(property, oldState.get(property));
    }

    public record Replaceable<T extends Conversion>(boolean replace, List<T> conversions) {
        public static <T extends Conversion> Codec<Replaceable<T>> codec(Codec<T> codec) {
            return RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.optionalFieldOf("replace", false).forGetter(Replaceable::replace),
                codec.listOf().fieldOf("conversions").forGetter(Replaceable::conversions)
            ).apply(instance, Replaceable::new)
            );
        }
    }
}
