package dev.enjarai.trickster.item;

import io.wispforest.lavender.book.LavenderBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GuidebookItem extends LavenderBookItem {
    public GuidebookItem(Settings settings, @NotNull Identifier bookId) {
        super(settings, bookId);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.trickster.guidebook.author")
            .styled(s -> s.withFormatting(Formatting.GRAY)));
        super.appendTooltip(stack, context, tooltip, type);
    }
}
