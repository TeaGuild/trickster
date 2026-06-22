package dev.enjarai.trickster.spell.trick.mana;

import dev.enjarai.trickster.net.ManaMoveParticlePacket;
import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.slot.StorageFragment;
import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;

public class PullManaTrick extends AbstractConduitTrick {
    public PullManaTrick() {
        super(Pattern.of(7, 4, 1, 0, 4, 2, 1, 3, 4, 5, 1, 6, 7, 8, 1));
    }

    @Override
    protected boolean isThereEvenAnyPointToTryingToRunThisTrickRightNowQuestionMark(SpellContext ctx) {
        for (var view : ctx.getManaStorage()) {
            if (view.getAmount() < view.getCapacity()) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected Storage<ManaVariant> getSource(SpellContext ctx, Storage<ManaVariant> storage) {
        return storage;
    }

    @Override
    protected Storage<ManaVariant> getTarget(SpellContext ctx, Storage<ManaVariant> storage) {
        return ctx.getManaStorage();
    }

    @Override
    protected void createParticles(SpellContext ctx, StorageFragment holder, ManaVariant variant, float amount) {
        var fromPos = holder.getSourceOrCasterPos(this, ctx);
        var toPos = ctx.source().getPos();

        var distance = fromPos.distance(toPos);
        if (distance < 0.5) {
            return;
        }

        ModNetworking.CHANNEL.serverHandle(ctx.source().getWorld()
            .getPlayers(p -> p.squaredDistanceTo(fromPos.x(), fromPos.y(), fromPos.z()) < 24 * 24))
            .send(new ManaMoveParticlePacket(toPos, fromPos, variant, amount));
    }
}
