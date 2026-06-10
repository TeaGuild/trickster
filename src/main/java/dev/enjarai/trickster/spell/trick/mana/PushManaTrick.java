package dev.enjarai.trickster.spell.trick.mana;

import dev.enjarai.trickster.net.ManaMoveParticlePacket;
import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.slot.StorageFragment;
import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import dev.enjarai.trickster.spell.mana.type.Manae;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public class PushManaTrick extends AbstractConduitTrick {
    public PushManaTrick() {
        super(Pattern.of(7, 8, 4, 6, 7, 5, 4, 3, 7, 2, 1, 0, 7, 4, 1));
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
            .send(new ManaMoveParticlePacket(fromPos, toPos, v));
    }

    @Override
    protected boolean isThereEvenAnyPointToTryingToRunThisTrickRightNowQuestionMark(SpellContext ctx, ManaVariant variant, boolean convert) {
        try (var trans = Transaction.openOuter()) {
            return ctx.getManaStorage().extract(variant, 1, trans) > 0;
        }
    }

    @Override
    protected long affect(SpellContext ctx, Storage<ManaVariant> target, ManaVariant variant, long limit, double taxPercentage, boolean convert, TransactionContext trans) {
        var self = ctx.source().getManaStorage();

        var conversionMultiplier = convert ? variant.getMana().getConversionMultiplier() : 1d;
        var taxedMult = 1 - taxPercentage;

        var taken = self.extract(variant, limit, trans);
        var inserted = target.insert(convert ? ManaVariant.of(Manae.TRADITIONAL) : variant, (long) (taken * conversionMultiplier * taxedMult), trans);
        self.insert(variant, taken - (long) (inserted / conversionMultiplier / taxedMult), trans);
        return (long) inserted;
    }
}
