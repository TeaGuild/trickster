package dev.enjarai.trickster.item.component;

import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;

public record FaceHeightComponent(int height) {
    public static final StructEndec<FaceHeightComponent> ENDEC = StructEndecBuilder.of(
        Endec.INT.fieldOf("height", FaceHeightComponent::height),
        FaceHeightComponent::new
    );
}
