package dev.enjarai.trickster.fleck;

import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.math.random.Random;

public record LineFleck(FleckPosition pos, FleckPosition pos2, float size, int color) implements Fleck, ScalableFleck, PaintableFleck {

    public static final Random colorsRandom = new LocalRandom(0xba115);

    static StructEndec<LineFleck> ENDEC = StructEndecBuilder.of(
        FleckPosition.ENDEC.fieldOf("pos", LineFleck::pos),
        FleckPosition.ENDEC.fieldOf("pos2", LineFleck::pos2),
        Endec.FLOAT.fieldOf("size", LineFleck::size),
        Endec.INT.fieldOf("size", LineFleck::color),
        LineFleck::new
    );

    //

    @Override
    public FleckType<?> type() {
        return FleckType.LINE;
    }

    @Override
    public ScalableFleck scaleFleck(double scale) {
        return new LineFleck(
            pos,
            pos2,
            (float) scale,
            color
        );
    }

    @Override
    public PaintableFleck paintFleck(int color) {
        return new LineFleck(
            pos,
            pos2,
            size,
            color
        );
    }

    @Override
    public int getColor() {
        return color;
    }
}
