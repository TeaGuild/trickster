package dev.enjarai.trickster.spell.trick.mana;

import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.net.ManaMoveParticlePacket;
import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.slot.SlotFragment;
import net.minecraft.item.ItemStack;

public class PullManaTrick extends AbstractConduitTrick {
    public PullManaTrick() {
        super(Pattern.of(7, 4, 1, 0, 4, 2, 1, 3, 4, 5, 1, 6, 7, 8, 1));
    }

    @Override
    protected void createParticles(SpellContext ctx, SlotFragment slot, float v) {
        var fromPos = slot.getSourceOrCasterPos(this, ctx);
        var toPos = ctx.source().getPos();
        ModNetworking.CHANNEL.serverHandle(ctx.source().getWorld()
            .getPlayers(p -> p.squaredDistanceTo(fromPos.x(), fromPos.y(), fromPos.z()) < 24 * 24))
            .send(new ManaMoveParticlePacket(toPos, fromPos, v));
    }

    @Override
    protected float affect(SpellContext ctx, ItemStack stack, float limit, double taxPercentage) {
        var comp = stack.get(ModComponents.MANA);

        if (comp == null)
            return 0;

        var taxedMult = 1 - (float) taxPercentage;

        var world = ctx.source().getWorld();
        var self = ctx.source().getManaPool();
        var target = comp.pool().makeClone(world);
        var taken = limit - target.use(limit, world);
        stack.set(ModComponents.MANA, comp.with(target));
        var leftover = self.refill(taken * taxedMult, world); //leftover is how much taxed mana could not be inserted
        leftover /= taxedMult; //untax the leftover mana; it wasnt transferred so it shouldnt be taxed
        target = stack.get(ModComponents.MANA).pool().makeClone(world);
        target.refill(leftover, world);
        stack.set(ModComponents.MANA, comp.with(target));
        return taken - leftover;
    }
}
