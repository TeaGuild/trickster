package dev.enjarai.trickster.spell.fragment.slot;

import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.net.StorageMoveParticlePacket;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.Trick;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.util.Optional;

public interface StorageFragment extends VariantingFragment {
    VariantType<?> variantType();

    Storage<?> getStorage(Trick<?> trick, SpellContext ctx);

    <T> Storage<T> getStorage(Trick<?> trick, SpellContext ctx, VariantType<T> variantType);

    Optional<Vector3dc> getSourcePos(Trick<?> trick, SpellContext ctx);

    Vector3dc getSourceOrCasterPos(Trick<?> trick, SpellContext ctx);

    default float getMoveCost(Trick<?> trickSource, SpellContext ctx, Vector3dc pos, long amount) throws BlunderException {
        return getSourcePos(trickSource, ctx)
            .map(sourcePos -> {
                var a = variantType().costMultiplier * amount;
                return (float) (pos.distance(sourcePos) * a * 0.5);
            })
            .orElse(0f);
    }

    default void incurCost(Trick<?> trick, SpellContext ctx, Vector3dc pos, long amountMoved) {
        ctx.useMana(trick, getMoveCost(trick, ctx, pos, amountMoved));
    }

    default void spawnMoveParticles(Trick<?> trick, SpellContext ctx, StorageFragment to, long amountMoved) {
        var toPos = to.getSourceOrCasterPos(trick, ctx);
        spawnMoveParticles(trick, ctx, toPos, amountMoved);
    }

    default void spawnMoveParticles(Trick<?> trick, SpellContext ctx, Vector3dc toPos, long amountMoved) {
        var fromPos = getSourceOrCasterPos(trick, ctx);

        var distance = fromPos.distance(toPos);
        if (distance < 1) {
            return;
        }

        var difference = toPos.sub(fromPos, new Vector3d());
        var direction = difference.normalize(new Vector3d());

        var trailLength = Math.min(distance / 2, 48);
        var fromTrailTarget = fromPos.add(direction.mul(trailLength, new Vector3d()), new Vector3d());
        var toTrailSource = toPos.add(direction.mul(-trailLength, new Vector3d()), new Vector3d());

        var world = ctx.source().getWorld();
        ModNetworking.CHANNEL.serverHandle(world
            .getPlayers(p -> p.squaredDistanceTo(fromPos.x(), fromPos.y(), fromPos.z()) < 24 * 24))
            .send(new StorageMoveParticlePacket(fromPos, fromTrailTarget, toPos, amountMoved));
        ModNetworking.CHANNEL.serverHandle(world
            .getPlayers(p -> p.squaredDistanceTo(toPos.x(), toPos.y(), toPos.z()) < 24 * 24))
            .send(new StorageMoveParticlePacket(toTrailSource, toPos, toPos, amountMoved));
    }
}
