package dev.enjarai.trickster.data.mana;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import io.wispforest.endec.Endec;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public interface ManaConversionCondition {
    RegistryKey<Registry<ManaConversionCondition>> REGISTRY_KEY = RegistryKey.ofRegistry(Trickster.id("mana_conversion_condition"));
    Registry<ManaConversionCondition> REGISTRY = FabricRegistryBuilder.createSimple(REGISTRY_KEY).buildAndRegister();
    Endec<ManaConversionCondition> ENDEC = MinecraftEndecs.ofRegistry(REGISTRY);

    boolean test(SpellContext ctx, ManaVariant variant);

    static void register() {
        register("storm_stable", new StormStableConversionCondition());
    }

    private static void register(String id, ManaConversionCondition condition) {
        Registry.register(REGISTRY, Trickster.id(id), condition);
    }
}
