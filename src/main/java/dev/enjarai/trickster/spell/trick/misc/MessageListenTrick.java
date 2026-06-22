package dev.enjarai.trickster.spell.trick.misc;

import dev.enjarai.trickster.item.ChannelItem;
import dev.enjarai.trickster.spell.EvaluationResult;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellExecutor;
import dev.enjarai.trickster.spell.blunder.ItemInvalidBlunder;
import dev.enjarai.trickster.spell.blunder.OutOfRangeBlunder;
import dev.enjarai.trickster.spell.execution.executor.MessageListenerSpellExecutor;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.slot.SlotFragment;
import dev.enjarai.trickster.spell.fragment.slot.VariantType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class MessageListenTrick extends Trick<MessageListenTrick> {
    public MessageListenTrick() {
        super(Pattern.of(4, 0, 7, 2, 4), Signature.of(FragmentType.NUMBER.optionalOfArg(), MessageListenTrick::run, RetType.ANY.listOfRet().executor()));
        overload(Signature.of(FragmentType.NUMBER.optionalOfArg(), FragmentType.SLOT, MessageListenTrick::runWithChannel,
            RetType.ANY.listOfRet().thisFunctionExistsSolelyForMessageListeningOnItemsBecauseWeAlreadyHadAnAbstractionForItAndWeReallyDontWantToReworkItSoThisWillHaveToDoHonestly()));
    }

    public SpellExecutor run(SpellContext ctx, Optional<NumberFragment> timeout) {
        return new MessageListenerSpellExecutor(ctx.state(), timeout.map(NumberFragment::asInt), Optional.empty());
    }

    public EvaluationResult runWithChannel(SpellContext ctx, Optional<NumberFragment> timeout, SlotFragment slot) {
        var resource = slot.getResource(this, ctx, VariantType.ITEM);
        var range = ctx.source().getPos().distance(slot.getSourceOrCasterPos(this, ctx));
        var item = resource.getItem();

        if (range > 16) {
            throw new OutOfRangeBlunder(this, 16, range);
        }

        if (item instanceof ChannelItem channelItem) {
            AtomicReference<EvaluationResult> result = new AtomicReference<>();
            slot.applyItemModifier(this, ctx, stack -> {
                result.set(channelItem.messageListenBehavior(this, ctx, stack, timeout.map(NumberFragment::asInt)));
                return stack;
            });
            return result.get();
        }

        throw new ItemInvalidBlunder(this);
    }

}
