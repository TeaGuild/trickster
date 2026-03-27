package dev.enjarai.trickster.mixin.client;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.config.TricksterConfigModel;
import dev.enjarai.trickster.screen.SpellSlotWidget;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> {
    public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Unique
    public int getXOffset(TricksterConfigModel.SpellSlotPositions pos, int i) {
        return switch (pos) {
            case TOP_LEFT, BOTTOM_LEFT -> 4 + i * 18;
            case TOP_RIGHT, BOTTOM_RIGHT -> 156 - i * 18;
            case LEFT_TOP, LEFT_BOTTOM -> -12;
            case RIGHT_TOP, RIGHT_BOTTOM -> 176;
            default -> 0;
        };
    }

    @Unique
    public int getYOffset(TricksterConfigModel.SpellSlotPositions pos, int i) {
        return switch (pos) {
            case TOP_LEFT, TOP_RIGHT -> -12;
            case BOTTOM_LEFT, BOTTOM_RIGHT -> 166;
            case LEFT_TOP, RIGHT_TOP -> 4 + i * 18;
            case LEFT_BOTTOM, RIGHT_BOTTOM -> 146 - i * 18;
            default -> 0;
        };
    }

    @Unique
    public int getUOffset(TricksterConfigModel.SpellSlotPositions pos) {
        return switch (pos) {
            case LEFT_TOP, LEFT_BOTTOM -> -4;
            case RIGHT_TOP, RIGHT_BOTTOM -> 4;
            default -> 0;
        };
    }

    @Unique
    public int getVOffset(TricksterConfigModel.SpellSlotPositions pos) {
        return switch (pos) {
            case TOP_LEFT, TOP_RIGHT -> -4;
            case BOTTOM_LEFT, BOTTOM_RIGHT -> 4;
            default -> 0;
        };
    }

    @Unique
    public List<SpellSlotWidget> spellSlots = new ArrayList<>();

    @Inject(
        method = "init", at = @At("TAIL")
    )
    private void initSpellSlots(CallbackInfo ci) {
        var position = Trickster.CONFIG.spellSlotPosition();
        if (position == TricksterConfigModel.SpellSlotPositions.HIDDEN) {
            return;
        }

        spellSlots = new ArrayList<>();
        var spellData = client.player.getComponent(ModEntityComponents.CASTER).getRunningSpellData();

        for (int i = 0; i < 5; i++) {
            var widget = new SpellSlotWidget(x + getXOffset(position, i), y + getYOffset(position, i), getUOffset(position), getVOffset(position), i);
            widget.updateState(spellData.get(i));
            spellSlots.add(widget);
            addDrawableChild(widget);

        }
    }

    @Inject(
        method = "handledScreenTick", at = @At("TAIL")
    )
    private void tickSpellSlots(CallbackInfo ci) {
        var position = Trickster.CONFIG.spellSlotPosition();
        var spellData = client.player.getComponent(ModEntityComponents.CASTER).getRunningSpellData();
        for (var slot : spellSlots) {
            slot.setPosition(x + getXOffset(position, slot.index), y + getYOffset(position, slot.index));
            slot.updateState(spellData.get(slot.index));
        }
    }
}
