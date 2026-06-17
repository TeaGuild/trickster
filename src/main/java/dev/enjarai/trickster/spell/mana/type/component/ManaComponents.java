package dev.enjarai.trickster.spell.mana.type.component;

import dev.enjarai.trickster.Trickster;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.UnaryOperator;

public class ManaComponents {
    public static final ComponentType<StabilityComponent> STABILITY = register(
        "stability", builder -> builder
            .endec(StabilityComponent.ENDEC)
            .cache()
    );
    public static final ComponentType<ColorComponent> COLOR = register(
        "color", builder -> builder
            .endec(ColorComponent.ENDEC)
            .cache()
    );

    @ApiStatus.Internal
    public static <T> ComponentType<T> register(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Trickster.id(id), (builderOperator.apply(ComponentType.builder())).build());
    }

    public static void register() {

    }
}
