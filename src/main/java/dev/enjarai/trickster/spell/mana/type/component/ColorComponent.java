package dev.enjarai.trickster.spell.mana.type.component;

import dev.enjarai.trickster.EndecTomfoolery;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import org.joml.Vector4dc;

import static net.minecraft.util.math.ColorHelper.Argb.*;

public record ColorComponent(double red, double green, double blue) {

    public static final Endec<ColorComponent> ENDEC = EndecTomfoolery.withAlternative(
        StructEndecBuilder.of(
            Endec.DOUBLE.fieldOf("red", ColorComponent::red),
            Endec.DOUBLE.fieldOf("green", ColorComponent::green),
            Endec.DOUBLE.fieldOf("blue", ColorComponent::blue),
            ColorComponent::new
        ),
        StructEndecBuilder.of(
            Endec.INT.fieldOf("color", ColorComponent::color),
            ColorComponent::fromInt
        )
    );

    public static ColorComponent fromInt(int color) {
        return new ColorComponent(getRed(color), getGreen(color), getBlue(color));
    }

    public static ColorComponent fromVector(Vector4dc color) {
        return new ColorComponent(color.y(), color.z(), color.w());
    }

    public int color() {
        return getArgb((int) red, (int) green, (int) blue);
    }
}
