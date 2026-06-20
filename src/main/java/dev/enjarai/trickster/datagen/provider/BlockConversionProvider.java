package dev.enjarai.trickster.datagen.provider;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import com.google.common.collect.Maps;

import com.mojang.serialization.Codec;
import dev.enjarai.trickster.data.conversion.BlockConversionLoader;
import dev.enjarai.trickster.data.conversion.Conversion;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Block;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

public abstract class BlockConversionProvider<T extends Conversion, B extends ThisIsABuilder<T>> implements DataProvider {
    protected final DataOutput.PathResolver pathResolver;

    private final String type;
    private final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture;
    private final Map<Identifier, B> builders = Maps.newLinkedHashMap();
    private final Codec<BlockConversionLoader.Replaceable<T>> conversionCodec;
    private final Supplier<B> builderFactory;

    public BlockConversionProvider(FabricDataOutput output, String type, Codec<T> conversionCodec, Supplier<B> builderFactory, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
        this.pathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "conversion/" + type);
        this.registryLookupFuture = registryLookupFuture;
        this.type = type;
        this.conversionCodec = BlockConversionLoader.Replaceable.codec(conversionCodec);
        this.builderFactory = builderFactory;
    }

    protected abstract void configure(RegistryWrapper.WrapperLookup wrapperLookup);

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        return this.getRegistryLookupFuture()
            .thenCompose(wrapperLookup -> CompletableFuture.allOf(
                this.builders
                    .entrySet()
                    .stream()
                    .map(
                        entry -> {
                            Identifier identifier = entry.getKey();
                            List<T> values = entry.getValue().build();
                            Path path = this.pathResolver.resolveJson(identifier);
                            return DataProvider.writeCodecToPath(
                                writer, wrapperLookup, conversionCodec,
                                new BlockConversionLoader.Replaceable<>(false, values), path
                            );
                        }
                    )
                    .toArray(CompletableFuture[]::new)
            ));
    }

    protected CompletableFuture<RegistryWrapper.WrapperLookup> getRegistryLookupFuture() {
        return this.registryLookupFuture.thenApply(lookup -> {
            this.builders.clear();
            this.configure(lookup);
            return lookup;
        });
    }

    protected B getOrCreateConversion(Block source) {
        Identifier id = Registries.BLOCK.getId(source);
        return this.builders.computeIfAbsent(id, identifier -> builderFactory.get());
    }

    @Override
    public String getName() {
        return "Block Conversion (" + type + ")";
    }
}
