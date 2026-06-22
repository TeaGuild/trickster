package dev.enjarai.trickster.spell.trick;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.compat.ModCompat;
import dev.enjarai.trickster.compat.inline.PatternData;
import dev.enjarai.trickster.spell.EvaluationResult;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.CantEditBlockBlunder;
import dev.enjarai.trickster.spell.blunder.InvalidInputsBlunder;
import dev.enjarai.trickster.spell.blunder.NotLoadedBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import dev.enjarai.trickster.spell.type.*;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3dc;

import java.util.*;

public abstract class Trick<T extends Trick<T>> {
    public static final Identifier TRICK_RANDOM = Trickster.id("trick");

    protected final Pattern pattern;
    private final List<Signature<T>> handlers;

    public Trick(Pattern pattern, List<Signature<T>> handlers) {
        this.pattern = pattern;
        this.handlers = handlers;
    }

    public Trick(Pattern pattern, Signature<T> primary) {
        this(pattern);
        this.handlers.add(primary);
    }

    public Trick(Pattern pattern) {
        this(pattern, new ArrayList<>());
    }

    public final Pattern getPattern() {
        return pattern;
    }

    public Trick<T> overload(Signature<T> signature) {
        this.handlers.add(signature);
        return this;
    }

    @SuppressWarnings("unchecked")
    public EvaluationResult activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        for (int i = handlers.size() - 1; i >= 0; i--) {
            var handler = handlers.get(i);

            if (handler.match(fragments)) {
                return handler.run((T) this, ctx, fragments);
            }
        }

        throw new InvalidInputsBlunder(this, fragments);
    }

    protected void expectCanBuild(SpellContext ctx, BlockPos... positions) {
        if (ctx.source().getPlayer().isEmpty()) {
            return;
        }

        var player = ctx.source().getPlayer().get();

        if (player.interactionManager.getGameMode().isBlockBreakingRestricted()) {
            throw new CantEditBlockBlunder(this, positions[0]);
        }

        expectLoaded(ctx, positions);
        for (var pos : positions) {
            if (!player.canModifyAt(ctx.source().getWorld(), pos)) {
                throw new CantEditBlockBlunder(this, pos);
            }
        }
    }

    public @Nullable Set<UUID> restricted() {
        return null;
    }

    public MutableText getName() {
        var id = Tricks.REGISTRY.getId(this);

        if (id == null) {
            return Text.literal("Unregistered");
        }

        return Text.literal("").append(
            Text.translatable(Trickster.MOD_ID + ".trick." + id.getNamespace() + "." + id.getPath())
                .withColor(FragmentType.PATTERN.color().getAsInt())
        );
    }

    public MutableText getIconAndName() {
        if (!ModCompat.INLINE_LOADED) return getName();
        var id = Tricks.REGISTRY.getId(this);
        return Text.empty().append(PatternData.make(pattern, PatternData.getStyle(pattern, false).withColor(FragmentType.PATTERN.color().getAsInt())).append(" ")
            .append(id == null ? Text.literal("Unregistered") : Text.translatable(Trickster.MOD_ID + ".trick." + id.getNamespace() + "." + id.getPath())));
    }

    public List<Signature<T>> getSignatures() {
        return handlers;
    }

    protected double getManaTransferTaxMultiplier(SpellContext ctx, Vector3dc pos) {
        var distance = ctx.source().getPos().distance(pos);
        float r = Trickster.CONFIG.manaTransferEfficiency();
        return 1 - Math.max(0, 1 - r / (distance + r - 16));
    }

    protected void expectLoaded(SpellContext ctx, BlockPos... positions) {
        for (var pos : positions) {
            if (!ctx.source().getWorld().isChunkLoaded(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()))) {
                throw new NotLoadedBlunder(this, pos);
            }
        }
    }

    protected long insertMixing(SpellContext ctx, Storage<ManaVariant> storage, ManaVariant variant, long maxAmount, TransactionContext trans) {
        long inserted = 0;

        for (var view : storage) {
            var targetResource = view.getResource();
            var viewMax = Math.min(view.getCapacity() - view.getAmount(), maxAmount - inserted);

            try (var trans2 = trans.openNested()) {
                long insertedThisTime = 0;

                if (targetResource.isBlank() || targetResource.equals(variant)) {
                    insertedThisTime = storage.insert(variant, viewMax, trans2);
                } else if (targetResource.isOf(variant.getMana())) {
                    insertedThisTime = storage.insert(targetResource, viewMax, trans2);

                    long total = view.extract(targetResource, Long.MAX_VALUE, trans2);
                    long targetAmount = total - insertedThisTime;
                    var finalVariant = variant.getMana().mix(ctx.source().getWorld(), variant, insertedThisTime, targetResource, targetAmount);
                    if (finalVariant.isPresent()) {
                        storage.insert(finalVariant.get(), total, trans2);
                    } else {
                        insertedThisTime = 0;
                    }
                }

                if (insertedThisTime > 0) {
                    trans2.commit();
                    inserted += insertedThisTime;
                }
            }
        }

        return inserted;
    }
}
