package dev.enjarai.trickster.spell.trick.list;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.Signature;
import io.vavr.collection.HashMap;

import java.util.List;

public class ListSizeTrick extends DistortionTrick<ListSizeTrick> {
    public ListSizeTrick() {
        super(Pattern.of(0, 2, 5, 4, 3, 0), Signature.of(ArgType.ANY.listOfArg(), ListSizeTrick::list, FragmentType.NUMBER));
        overload(Signature.of(ArgType.ANY.mappedTo(ArgType.ANY), ListSizeTrick::map, FragmentType.NUMBER));
    }

    public NumberFragment list(SpellContext ctx, List<Fragment> list) {
        return new NumberFragment(list.size());
    }

    public NumberFragment map(SpellContext ctx, HashMap<Fragment, Fragment> map) {
        return new NumberFragment(map.size());
    }
}
