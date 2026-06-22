package dev.enjarai.trickster.spell.mana.type.component;

import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;

public record ColorComponent(int color) {
    public static final StructEndec<ColorComponent> ENDEC = StructEndecBuilder.of(
        Endec.INT.fieldOf("color", ColorComponent::color),
        ColorComponent::new
    );
}
