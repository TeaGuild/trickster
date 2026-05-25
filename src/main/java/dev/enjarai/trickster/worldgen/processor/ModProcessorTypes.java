package dev.enjarai.trickster.worldgen.processor;

import com.mojang.serialization.MapCodec;

import dev.enjarai.trickster.Trickster;
import io.wispforest.endec.StructEndec;
import io.wispforest.owo.serialization.CodecUtils;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;

public class ModProcessorTypes {
    public static final StructureProcessorType<OxidizationProcessor> OXIDIZE = register("oxidize", OxidizationProcessor.ENDEC);

    public static void register() {
        // init class
    }

    @SuppressWarnings("unchecked")
    private static <P extends StructureProcessor> StructureProcessorType<P> register(String name, StructEndec<P> endec) {
        var mapCodec = (MapCodec<StructureProcessor>) CodecUtils.toMapCodec(endec);
        return (StructureProcessorType<P>) Registry.register(Registries.STRUCTURE_PROCESSOR, Trickster.id(name), () -> mapCodec);
    }
}
