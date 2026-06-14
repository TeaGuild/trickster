package dev.enjarai.trickster.screen.owo;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.render.CircleRenderer;
import dev.enjarai.trickster.render.PatternRenderer;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.Tricks;
import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIModelParsingException;
import io.wispforest.owo.ui.parsing.UIParsing;
import org.w3c.dom.Element;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class GlyphComponent extends BaseComponent {
    protected Pattern pattern;
    protected List<Integer> patternList;
    protected int size;

    public GlyphComponent(Trick<?> trick, int size) {
        this(trick.getPattern(), size);
    }

    public GlyphComponent(Pattern pattern, int size) {
        super();
        this.pattern = pattern;
        this.size = size;
        this.patternList = pattern
            .entries().stream()
            .flatMapToInt(e -> IntStream.of(e.p1(), e.p2()))
            .distinct()
            .boxed()
            .toList();
    }

    private static final CircleRenderer delegate = new CircleRenderer(false, false, 0);
    static {
        delegate.setColor(0f, 0f, 0f);
        delegate.setRenderTime(0);
    }

    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        var patternSize = size / 2;

        PatternRenderer.renderPattern(
            pattern, context.getMatrices(), context.getVertexConsumers(), x + patternSize + 4, y + patternSize + 4,
            patternSize, true, true, Trickster.CONFIG.dotSize(), 0.9f, 0.5f, delegate, null
        );
    }

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        return size + 8;
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        return size + 8;
    }

    public static GlyphComponent parseTrick(Element element) {
        UIParsing.expectAttributes(element, "trick-id");
        UIParsing.expectAttributes(element, "size");

        var trickId = UIParsing.parseIdentifier(element.getAttributeNode("trick-id"));
        var trick = Tricks.REGISTRY.get(trickId);

        if (trick == null) {
            throw new UIModelParsingException("Not a valid trick: " + trickId);
        }

        var size = UIParsing.parseUnsignedInt(element.getAttributeNode("size"));

        return new GlyphComponent(trick, size);
    }

    public static GlyphComponent parseList(Element element) {
        UIParsing.expectAttributes(element, "pattern");
        UIParsing.expectAttributes(element, "size");

        var patternString = element.getAttributeNode("pattern").getTextContent();

        var pattern = Pattern.from(
            Arrays.stream(patternString.split(","))
                .map(s -> Byte.valueOf(s, 10)).toList()
        );

        var size = UIParsing.parseUnsignedInt(element.getAttributeNode("size"));

        return new GlyphComponent(pattern, size);
    }
}
