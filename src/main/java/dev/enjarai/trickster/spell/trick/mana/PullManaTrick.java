package dev.enjarai.trickster.spell.trick.mana;

import dev.enjarai.trickster.net.ManaMoveParticlePacket;
import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.slot.StorageFragment;
import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import dev.enjarai.trickster.spell.mana.type.Mana;
import dev.ryanhcode.sable.companion.SableCompanion;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import org.joml.Vector3d;

public class PullManaTrick extends AbstractConduitTrick {
    public PullManaTrick() {
        super(Pattern.of(7, 4, 1, 0, 4, 2, 1, 3, 4, 5, 1, 6, 7, 8, 1));
    }

    @Override
    protected void createParticles(SpellContext ctx, StorageFragment slot, Mana mana, float v) {
        var fromPos = SableCompanion.INSTANCE.projectOutOfSubLevel(ctx.source().getWorld(), slot.getSourceOrCasterPos(this, ctx), new Vector3d());
        var toPos = ctx.getGlobalPos();

        var distance = fromPos.distance(toPos);
        if (distance < 0.5) {
            return;
        }

        ModNetworking.CHANNEL.serverHandle(ctx.source().getWorld()
            .getPlayers(p -> p.squaredDistanceTo(fromPos.x(), fromPos.y(), fromPos.z()) < 24 * 24))
            .send(new ManaMoveParticlePacket(toPos, fromPos, mana, v));
    }

    @Override
    protected boolean isThereEvenAnyPointToTryingToRunThisTrickRightNowQuestionMark(SpellContext ctx, Mana mana) {
        for (var view : ctx.getManaStorage()) {
            if (view.getAmount() < view.getCapacity()) {
                if (view.isResourceBlank() || view.getResource().getMana().equals(mana)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected long affect(SpellContext ctx, Storage<ManaVariant> target, Mana mana, long limit, double taxPercentage, TransactionContext trans) {
        var self = ctx.source().getManaStorage();

        var taxedMult = 1 - taxPercentage;

        long result = 0;
        long taken = 0;

        for (var view : target) {
            if (taken < limit) {
                var left = view.getResource();

                if (left.getMana().equals(mana)) {
                    long extracted = target.extract(left, limit - taken, trans);

                    long given = 0;

                    for (var view2 : self) {
                        var right = view2.getResource();

                        if (given < extracted) {
                            if (right.isBlank()) {
                                long inserted = self.insert(left, (long) (extracted * taxedMult), trans);
                                long meow = (long) (inserted / taxedMult);
                                target.insert(left, extracted - meow, trans);

                                given += meow;
                                taken += meow;
                                result += inserted;
                            } else if (right.getMana().equals(mana)) {
                                long inserted = self.insert(right, (long) (extracted * taxedMult), trans);
                                long total = self.extract(right, Long.MAX_VALUE, trans);
                                var finalVariant = mana.mix(ctx.source().getWorld(), left, inserted, right, total - inserted);
                                if (finalVariant.isPresent()) {
                                    self.insert(finalVariant.get(), total, trans);
                                } else {
                                    self.insert(right, total - inserted, trans);
                                    inserted = self.insert(left, inserted, trans);
                                }

                                long meow = (long) (inserted / taxedMult);
                                target.insert(left, extracted - meow, trans);

                                given += meow;
                                taken += meow;
                                result += inserted;
                            }
                        }
                    }
                }
            }
        }

        return result;
    }
}
