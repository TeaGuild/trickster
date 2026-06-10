package dev.enjarai.trickster.spell.trick.mana;

import dev.enjarai.trickster.net.ManaMoveParticlePacket;
import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.slot.StorageFragment;
import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import dev.enjarai.trickster.spell.mana.type.Mana;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public class PushManaTrick extends AbstractConduitTrick {
    public PushManaTrick() {
        super(Pattern.of(7, 8, 4, 6, 7, 5, 4, 3, 7, 2, 1, 0, 7, 4, 1));
    }

    @Override
    protected void createParticles(SpellContext ctx, StorageFragment slot, Mana mana, float v) {
        var fromPos = slot.getSourceOrCasterPos(this, ctx);
        var toPos = ctx.source().getPos();

        var distance = fromPos.distance(toPos);
        if (distance < 0.5) {
            return;
        }

        ModNetworking.CHANNEL.serverHandle(ctx.source().getWorld()
            .getPlayers(p -> p.squaredDistanceTo(fromPos.x(), fromPos.y(), fromPos.z()) < 24 * 24))
            .send(new ManaMoveParticlePacket(fromPos, toPos, mana, v));
    }

    @Override
    protected boolean isThereEvenAnyPointToTryingToRunThisTrickRightNowQuestionMark(SpellContext ctx, Mana mana) {
        for (var view : ctx.getManaStorage()) {
            if (view.getResource().getMana().equals(mana) && view.getAmount() > 0) {
                return true;
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

        for (var view : self) {
            if (taken < limit) {
                var left = view.getResource();

                if (left.getMana().equals(mana)) {
                    long extracted = self.extract(left, limit - taken, trans);

                    long given = 0;

                    for (var view2 : target) {
                        var right = view2.getResource();

                        if (given < extracted) {
                            if (right.isBlank()) {
                                long inserted = target.insert(left, (long) (extracted * taxedMult), trans);
                                long meow = (long) (inserted / taxedMult);
                                self.insert(left, extracted - meow, trans);

                                given += meow;
                                taken += meow;
                                result += inserted;
                            } else if (right.getMana().equals(mana)) {
                                long inserted = target.insert(right, (long) (extracted * taxedMult), trans);
                                long total = target.extract(right, Long.MAX_VALUE, trans);
                                var finalVariant = mana.mix(left, inserted, right, total - inserted);
                                if (finalVariant.isPresent()) {
                                    target.insert(finalVariant.get(), total, trans);
                                } else {
                                    target.insert(right, total - inserted, trans);
                                    inserted = target.insert(left, inserted, trans);
                                }

                                long meow = (long) (inserted / taxedMult);
                                self.insert(left, extracted - meow, trans);

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
