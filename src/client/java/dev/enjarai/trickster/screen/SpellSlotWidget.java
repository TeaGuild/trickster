package dev.enjarai.trickster.screen;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.cca.CasterComponent;
import dev.enjarai.trickster.net.KillSpellPacket;
import dev.enjarai.trickster.net.ModNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class SpellSlotWidget extends ButtonWidget {
    public static final Identifier TEXTURE_HOVER = Trickster.id("textures/gui/sprites/spell_slot/hover.png");
    public static final Identifier TEXTURE_SUSPENDED = Trickster.id("suspended");
    public static final Identifier TEXTURE_OK = Trickster.id("ok");
    public static final Identifier TEXTURE_PARTIAL = Trickster.id("partial");
    public static final Identifier TEXTURES_FULL = Trickster.id("full");
    public static final Identifier TEXTURES_ERROR = Trickster.id("error");

    public final int index;
    public State currentState = State.INACTIVE;
    private final int u;
    private final int v;

    public SpellSlotWidget(int x, int y, int u, int v, int index) {
        super(x, y, 16 - Math.abs(u), 16 - Math.abs(v), Text.empty(), btn -> {}, DEFAULT_NARRATION_SUPPLIER);
        this.index = index;
        this.u = u;
        this.v = v;
    }

    @Override
    public void onPress() {
        if (currentState != State.INACTIVE) {
            ModNetworking.CHANNEL.clientHandle().send(new KillSpellPacket(index));
        }
    }

    @Override
    protected boolean isValidClickButton(int button) {
        return super.isValidClickButton(button) && currentState != State.INACTIVE;
    }

    @Override
    public boolean isSelected() {
        return super.isSelected() && currentState != State.INACTIVE;
    }

    public void updateState(@Nullable CasterComponent.RunningSpellData spellData) {
        if (spellData == null) {
            currentState = State.INACTIVE;
        } else if (spellData.errored()) {
            currentState = State.ACTIVE_ERROR;
        } else if (spellData.executionsLastTick() >= Trickster.CONFIG.maxExecutionsPerSpellPerTick()) {
            currentState = State.ACTIVE_FULL;
        } else if (spellData.executionsLastTick() >= Trickster.CONFIG.maxExecutionsPerSpellPerTick() / 2) {
            currentState = State.ACTIVE_PARTIAL;
        } else if (spellData.executionsLastTick() <= 0) {
            currentState = State.ACTIVE_SUSPENDED;
        } else {
            currentState = State.ACTIVE_OK;
        }

        if (spellData != null && spellData.message().isPresent()) {
            setTooltip(Tooltip.of(spellData.message().get()));
        } else {
            setTooltip(null);
        }
    }

    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (currentState == State.INACTIVE) {
            context.drawTexture(currentState.texture,
                this.getX() + (u < 0 ? 8 : 0),
                this.getY() + (v < 0 ? 8 : 0),
                Math.max(0, u) + (u > 0 ? -8 : 0),
                Math.max(0, v) + (v > 0 ? -8 : 0),
                this.width + (u != 0 ? -8 : 0),
                this.height + (v != 0 ? -8 : 0),
                16, 16
            );
            return;
        }

        if (this.isSelected()) {
            context.drawTexture(TEXTURE_HOVER,
                this.getX() + (u < 0 ? -1 : 0),
                this.getY() + (v < 0 ? -1 : 0),
                Math.max(0, u) + (u > 0 ? -1 : 0),
                Math.max(0, v) + (v > 0 ? -1 : 0),
                this.width + (u != 0 ? 1 : 0),
                this.height + (v != 0 ? 1 : 0),
                16, 16
            );
        }

        context.drawTexture(currentState.texture,
            this.getX() + (this.isSelected() ? Integer.signum(u) : 0),
            this.getY() + (this.isSelected() ? Integer.signum(v) : 0),
            Math.max(0, u),
            Math.max(0, v),
            this.width,
            this.height,
            16, 16
        );
    }

    public enum State {
        INACTIVE(TEXTURES_ERROR),
        ACTIVE_SUSPENDED(TEXTURE_SUSPENDED),
        ACTIVE_OK(TEXTURE_OK),
        ACTIVE_PARTIAL(TEXTURE_PARTIAL),
        ACTIVE_FULL(TEXTURES_FULL),
        ACTIVE_ERROR(TEXTURES_ERROR);

        public final Identifier texture;

        State(Identifier texture) {
            this.texture = texture.withPrefixedPath("textures/gui/sprites/spell_slot/").withSuffixedPath(".png");
        }
    }
}
