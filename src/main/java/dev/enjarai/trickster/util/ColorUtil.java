package dev.enjarai.trickster.util;

import dev.enjarai.trickster.Trickster;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.joml.Vector4d;

import static net.minecraft.util.math.ColorHelper.Argb.getAlpha;
import static net.minecraft.util.math.ColorHelper.Argb.getArgb;
import static net.minecraft.util.math.ColorHelper.Argb.getBlue;
import static net.minecraft.util.math.ColorHelper.Argb.getGreen;
import static net.minecraft.util.math.ColorHelper.Argb.getRed;

public class ColorUtil {
    public static int mixColors(int colorLeft, long amountLeft, int colorRight, long amountRight) {
        long m = amountLeft + amountRight;

        long r = amountLeft * getRed(colorLeft) + amountRight * getRed(colorRight);
        long g = amountLeft * getGreen(colorLeft) + amountRight * getGreen(colorRight);
        long b = amountLeft * getBlue(colorLeft) + amountRight * getBlue(colorRight);
        long a = amountLeft * getAlpha(colorLeft) + amountRight * getAlpha(colorRight);
        long l = amountLeft * Math.max(getRed(colorLeft), Math.max(getGreen(colorLeft), getBlue(colorLeft)))
            + amountRight * Math.max(getRed(colorRight), Math.max(getGreen(colorRight), getBlue(colorRight)));

        r /= m;
        g /= m;
        b /= m;

        double f = (double) l / (double) m;
        double s = (double) Math.max(r, Math.max(g, b));

        return getArgb(
            (int) (a / m),
            (int) ((double) r * f / s),
            (int) ((double) g * f / s),
            (int) ((double) b * f / s)
        );
    }

    public static Vector4d mixColors(double colorLeftRed, double colorLeftGreen, double colorLeftBlue, long amountLeft, double colorRightRed, double colorRightGreen, double colorRightBlue,
        long amountRight) {
        return mixColors(
            colorLeftRed, colorLeftGreen, colorLeftBlue, 255, amountLeft,
            colorRightRed, colorRightGreen, colorRightBlue, 255, amountRight
        );
    }

    public static Vector4d mixColors(double colorLeftRed, double colorLeftGreen, double colorLeftBlue, double colorLeftAlpha, long amountLeft, double colorRightRed, double colorRightGreen,
        double colorRightBlue, double colorRightAlpha, long amountRight) {
        long m = amountLeft + amountRight;

        double r = amountLeft * colorLeftRed + amountRight * colorRightRed;
        double g = amountLeft * colorLeftGreen + amountRight * colorRightGreen;
        double b = amountLeft * colorLeftBlue + amountRight * colorRightBlue;
        double a = amountLeft * colorLeftAlpha + amountRight * colorRightAlpha;
        double l = amountLeft * Math.max(colorLeftRed, Math.max(colorLeftGreen, colorLeftBlue))
            + amountRight * Math.max(colorRightRed, Math.max(colorRightGreen, colorRightBlue));

        r /= m;
        g /= m;
        b /= m;

        double f = l / (double) m;
        double s = Math.max(r, Math.max(g, b));

        return new Vector4d(
            a / m,
            r * f / s,
            g * f / s,
            b * f / s
        );
    }

    public static double distance(int colorLeft, int colorRight) {
        return Math.sqrt(
            Math.pow(getRed(colorLeft) - getRed(colorRight), 2)
                + Math.pow(getGreen(colorLeft) - getGreen(colorRight), 2)
                + Math.pow(getBlue(colorLeft) - getBlue(colorRight), 2)
                + Math.pow(getAlpha(colorLeft) - getAlpha(colorRight), 2)
        ) / 510.0;
    }

    public static MutableText rgbAsText(int color) {
        return Text.literal("%1o%1o%1o".formatted(
            getRed(color) / 32,
            getGreen(color) / 32,
            getBlue(color) / 32
        )).styled(s -> s.withFont(Trickster.FRAGMENT_FONT));
    }

    public static MutableText argbAsText(int color) {
        return Text.literal("%1o|%1o%1o%1o".formatted(
            getAlpha(color) / 32,
            getRed(color) / 32,
            getGreen(color) / 32,
            getBlue(color) / 32
        )).styled(s -> s.withFont(Trickster.FRAGMENT_FONT));
    }

    public static int lighten(int color, float amount) {
        return getArgb(
            getAlpha(color),
            (int) (getRed(color) * (1 - amount) + amount * 255),
            (int) (getGreen(color) * (1 - amount) + amount * 255),
            (int) (getBlue(color) * (1 - amount) + amount * 255)
        );
    }
}
