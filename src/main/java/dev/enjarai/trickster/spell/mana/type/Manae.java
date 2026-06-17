package dev.enjarai.trickster.spell.mana.type;

import dev.enjarai.trickster.Trickster;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.component.ComponentMap;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

import org.jetbrains.annotations.ApiStatus;

public class Manae {
    public static final RegistryKey<Registry<Mana>> REGISTRY_KEY = RegistryKey.ofRegistry(Trickster.id("mana"));
    public static final Registry<Mana> REGISTRY = FabricRegistryBuilder.createSimple(REGISTRY_KEY).buildAndRegister();

    public static final Mana EMPTY = register("empty", new SimpleMana(ComponentMap.EMPTY, 0x222222));
    public static final Mana TRADITIONAL = register("traditional", new SimpleMana(ComponentMap.EMPTY, 0xbb99ff));
    public static final StormMana STORM = register("storm", new StormMana());
    public static final ColorMana COLOR = register("color", new ColorMana());

    @ApiStatus.Internal
    public static <T extends Mana> T register(String path, T mana) {
        return Registry.register(REGISTRY, Trickster.id(path), mana);
    }

    public static void register() {

    }
}
