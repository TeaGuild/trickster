package dev.enjarai.trickster.datagen;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import dev.enjarai.trickster.Trickster;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorList;

public class ModProcessorListGenerator extends FabricDynamicRegistryProvider {
    public ModProcessorListGenerator(FabricDataOutput output, CompletableFuture<WrapperLookup> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    @Override
    public String getName() {
        return "Processor Lists";
    }

    @Override
    protected void configure(WrapperLookup registries, Entries entries) {}

    private static RegistryKey<StructureProcessorList> register(FabricDynamicRegistryProvider.Entries entries, String name, List<StructureProcessor> list) {
        var key = key(name);
        entries.add(key, new StructureProcessorList(list));
        return key;
    }

    private static RegistryKey<StructureProcessorList> key(String name) {
        return RegistryKey.of(RegistryKeys.PROCESSOR_LIST, Trickster.id(name));
    }
}
