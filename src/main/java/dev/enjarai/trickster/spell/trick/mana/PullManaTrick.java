package dev.enjarai.trickster.spell.trick.mana;

import dev.enjarai.trickster.net.ManaMoveParticlePacket;
import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.slot.StorageFragment;
import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public class PullManaTrick extends AbstractConduitTrick {
    public PullManaTrick() {
        super(Pattern.of(7, 4, 1, 0, 4, 2, 1, 3, 4, 5, 1, 6, 7, 8, 1));
    }

    @Override
    protected void createParticles(SpellContext ctx, StorageFragment slot, float v) {
        var fromPos = slot.getSourceOrCasterPos(this, ctx);
        var toPos = ctx.source().getPos();

        var distance = fromPos.distance(toPos);
        if (distance < 0.5) {
            return;
        }

        ModNetworking.CHANNEL.serverHandle(ctx.source().getWorld()
            .getPlayers(p -> p.squaredDistanceTo(fromPos.x(), fromPos.y(), fromPos.z()) < 24 * 24))
            .send(new ManaMoveParticlePacket(toPos, fromPos, v));
    }

    @Override
    protected boolean isThereEvenAnyPointToTryingToRunThisTrickRightNowQuestionMark(SpellContext ctx, ManaVariant variant) {
        try (var trans = Transaction.openOuter()) {
            return ctx.getManaStorage().insert(variant, 1, trans) > 0;
        }
    }

    @Override
    protected long affect(SpellContext ctx, Storage<ManaVariant> target, ManaVariant variant, long limit, double taxPercentage, long count, TransactionContext trans) {
        var self = ctx.source().getManaStorage();

        var taxedMult = 1 - taxPercentage;

        var taken = target.extract(variant, limit, trans);
        var leftover = taken - self.insert(variant, (long) (taken * taxedMult * count), trans); //leftover is how much taxed mana could not be inserted
        leftover /= count;
        leftover /= taxedMult; //untax the leftover mana; it wasnt transferred so it shouldnt be taxed
        target.insert(variant, leftover, trans);
        return taken - leftover;
    }
}
