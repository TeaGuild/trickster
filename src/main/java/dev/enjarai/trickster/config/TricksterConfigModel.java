package dev.enjarai.trickster.config;

import io.wispforest.owo.config.annotation.*;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.config.Option;

@Modmenu(modId = "trickster")
@Config(name = "trickster-config", wrapperName = "TricksterConfig")
public class TricksterConfigModel {
    @SectionHeader("accessibility")

    public boolean dotEmphasis = false;
    public Color dotEmphasisColor = new Color(0.5f, 0.2f, 0.4f, 1.0f);

    @WithAlpha
    public Color subcircleDividerPinColor = new Color(0.5f, 0.5f, 1f, 0.2f);

    @SectionHeader("client")

    public boolean topHatInterceptScrolling = false;
    public boolean invertTopHatScrolling = false;
    public boolean allowScrollInSpellScreen = true;
    public double keyZoomSpeed = 1.0;
    public SpellSlotPositions spellSlotPosition = SpellSlotPositions.BOTTOM_RIGHT;
    public SpellSlotPositions creativeSpellSlotPosition = SpellSlotPositions.RIGHT_BOTTOM;

    @SectionHeader("server")

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @PredicateConstraint("requirePositive")
    public int maxExecutionsPerSpellPerTick = 64;

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @PredicateConstraint("requirePositive")
    public float maxBlockBreakingHardness = 55.5f;

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public boolean allowMovingUnbreakableBlock = true;

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public boolean allowMovingExtendedPiston = true;

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @PredicateConstraint("requirePositive")
    public float whorlRechargeRate = 0.5f;

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @PredicateConstraint("requirePositive")
    public float whorlMaxMana = 512.0f;

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @PredicateConstraint("requirePositive")
    public float manaTransferEfficiency = 100.0f;

    @SectionHeader("circle-customization")

    @PredicateConstraint("requirePositive")
    public float patternSize = 1f;
    @PredicateConstraint("requirePositive")
    public float dotSize = 1f;
    @PredicateConstraint("requirePositive")
    public float circleOpacity = 0.8f;
    @PredicateConstraint("requirePositive")
    public float ponderOpacity = 0.1f;
    @PredicateConstraint("requirePositive")
    public float patternOpacity = 0.7f;
    @PredicateConstraint("requirePositive")
    public float patternEdgeGap = 1f;
    public boolean outlinePatterns = false;

    @SectionHeader("aurora-client")

    public boolean dragDrawing = false;
    public boolean barsHorizontal = false;

    @Sync(Option.SyncMode.INFORM_SERVER)
    public boolean disableOffhandScrollOpening = false;

    @RangeConstraint(min = 0, max = 1)
    public float adjacentPixelCollisionOffset = 0.25f;

    public static boolean requirePositive(int value) {
        return value >= 0;
    }

    public static boolean requirePositive(float value) {
        return value >= 0;
    }

    public enum SpellSlotPositions {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        LEFT_TOP,
        LEFT_BOTTOM,
        RIGHT_TOP,
        RIGHT_BOTTOM,
        HIDDEN
    }
}
