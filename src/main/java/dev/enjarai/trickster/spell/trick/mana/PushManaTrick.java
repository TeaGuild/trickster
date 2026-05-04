package dev.enjarai.trickster.spell.trick.mana;

import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.net.ManaMoveParticlePacket;
import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.slot.SlotFragment;
import net.minecraft.item.ItemStack;

public class PushManaTrick extends AbstractConduitTrick {
    public PushManaTrick() {
        super(Pattern.of(7, 8, 4, 6, 7, 5, 4, 3, 7, 2, 1, 0, 7, 4, 1));
    }

    @Override
    protected void createParticles(SpellContext ctx, SlotFragment slot, float v) {
        var fromPos = slot.getSourceOrCasterPos(this, ctx);
        var toPos = ctx.source().getPos();
        ModNetworking.CHANNEL.serverHandle(ctx.source().getWorld()
            .getPlayers(p -> p.squaredDistanceTo(fromPos.x(), fromPos.y(), fromPos.z()) < 24 * 24))
            .send(new ManaMoveParticlePacket(fromPos, toPos, v));
    }

    @Override
    protected float affect(SpellContext ctx, ItemStack stack, float limit, double taxPercentage) {
        var comp = stack.get(ModComponents.MANA);

        if (comp == null)
            return 0;

        var taxedMult = 1 - (float) taxPercentage;

        var world = ctx.source().getWorld();
        var self = ctx.source().getManaPool();
        var taken = limit - self.use(limit, world);
        comp = stack.get(ModComponents.MANA);
        var target = comp.pool().makeClone(world);
        var leftover = target.refill(taken * taxedMult, world); //leftover is how much taxed mana could not be inserted
        leftover /= taxedMult; //untax the leftover mana; it wasnt transferred so it shouldnt be taxed
        stack.set(ModComponents.MANA, comp.with(target));
        self.refill(leftover, world);
        return taken - leftover;
    }
}
