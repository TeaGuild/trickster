package dev.enjarai.trickster.spell.trick.inventory;

import java.util.List;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.slot.ContainerFragment;
import dev.enjarai.trickster.spell.fragment.slot.ResourceVariantFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.Signature;

public class FilterContainerTrick extends Trick<FilterContainerTrick> {
    public FilterContainerTrick() {
        super(
            Pattern.of(0, 1, 3, 0, 6, 7, 4, 1, 2, 5, 1),
            Signature.of(
                FragmentType.CONTAINER,
                ArgType.simple(ResourceVariantFragment.class).variadicOfArg().unpack(),
                FilterContainerTrick::ifAllElseFails,
                FragmentType.CONTAINER
            )
        );
    }

    public ContainerFragment ifAllElseFails(SpellContext ctx, ContainerFragment container, List<ResourceVariantFragment> filter) {
        return container.filtered(filter);
    }
}
