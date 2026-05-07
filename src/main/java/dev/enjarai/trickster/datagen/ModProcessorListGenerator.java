package dev.enjarai.trickster.datagen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import dev.enjarai.trickster.Trickster;
import dev.worldgen.lithostitched.api.worldgen.processor.LithostitchedProcessors;
import dev.worldgen.lithostitched.api.worldgen.processor.RandomSettings;
import dev.worldgen.lithostitched.api.worldgen.processor.enums.BlockType;
import dev.worldgen.lithostitched.api.worldgen.processor.enums.RandomMode;
import dev.worldgen.lithostitched.api.worldgen.processorcondition.LithostitchedProcessorConditions;
import dev.worldgen.lithostitched.worldgen.processor.ConditionProcessor;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.block.Oxidizable;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.rule.blockentity.PassthroughRuleBlockEntityModifier;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class ModProcessorListGenerator extends FabricDynamicRegistryProvider {
    public ModProcessorListGenerator(FabricDataOutput output, CompletableFuture<WrapperLookup> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    @Override
    public String getName() {
        return "Processor Lists";
    }

    @Override
    protected void configure(WrapperLookup registries, Entries entries) {
        var conditions = new ArrayList<StructureProcessor>();

        for (var kv : Oxidizable.OXIDATION_LEVEL_INCREASES.get().entrySet()) {
            var possibleOps = new ArrayList<RegistryEntry<StructureProcessorList>>();
            var fromId = Registries.BLOCK.getEntry(kv.getKey()).getKey().get().getValue();
            var next = Optional.of(kv.getValue());

            while (next.isPresent()) {
                var current = next.get();
                var toId = Registries.BLOCK.getEntry(current).getKey().get().getValue();

                possibleOps.add(entries.ref(register(entries, "oxidized/" + fromId.getNamespace() + "/" + fromId.getPath() + "/to/" + toId.getNamespace() + "/" + toId.getPath(), List.of(
                    LithostitchedProcessors.setBlock(
                        BlockStateProvider.of(current),
                        true,
                        RandomMode.PER_BLOCK,
                        PassthroughRuleBlockEntityModifier.INSTANCE
                    )
                ))));

                next = Oxidizable.getIncreasedOxidationBlock(current);
            }

            conditions.add(new ConditionProcessor(
                new RandomSettings(RandomMode.PER_BLOCK),
                LithostitchedProcessorConditions.allOf(
                    LithostitchedProcessorConditions.matchingBlocks(
                        kv.getKey(),
                        new StatePredicate(List.of()),
                        BlockType.INPUT
                    )
                ),
                List.of(
                    LithostitchedProcessors.applyRandom(RegistryEntryList.of(possibleOps), new RandomSettings(RandomMode.PER_BLOCK))
                ),
                List.of()
            ));
        }

        register(entries, "oxidize", conditions);
    }

    private static RegistryKey<StructureProcessorList> register(FabricDynamicRegistryProvider.Entries entries, String name, List<StructureProcessor> list) {
        var key = key(name);
        entries.add(key, new StructureProcessorList(list));
        return key;
    }

    private static RegistryKey<StructureProcessorList> key(String name) {
        return RegistryKey.of(RegistryKeys.PROCESSOR_LIST, Trickster.id(name));
    }
}
