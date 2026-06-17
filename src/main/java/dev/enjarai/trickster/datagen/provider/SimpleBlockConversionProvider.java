package dev.enjarai.trickster.datagen.provider;

import dev.enjarai.trickster.data.conversion.SimpleBlockConversionLoader;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public abstract class SimpleBlockConversionProvider extends BlockConversionProvider<SimpleBlockConversionLoader.WeightedValue, SimpleBlockConversionBuilder> {
    public SimpleBlockConversionProvider(FabricDataOutput output, String type, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
        super(output, type, SimpleBlockConversionLoader.WeightedValue.CODEC, SimpleBlockConversionBuilder::create, registryLookupFuture);
    }
}
