package dev.enjarai.trickster.item.component;

import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;

public record DesiredColorComponent(int color) {
    public static final Endec<DesiredColorComponent> ENDEC = StructEndecBuilder.of(
        Endec.INT.fieldOf("color", DesiredColorComponent::color),
        DesiredColorComponent::new
    );
}
