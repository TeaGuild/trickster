package dev.enjarai.trickster.item;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.SignChangingItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class LuminousBottleItem extends BottledItem implements SignChangingItem {
    public LuminousBottleItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean useOnSign(World world, SignBlockEntity signBlockEntity, boolean front, PlayerEntity player) {
        if (signBlockEntity.changeText(text -> text.withGlowing(true), front)) {
            player.getInventory().offerOrDrop(Items.GLASS_BOTTLE.getDefaultStack());
            world.playSound(null, signBlockEntity.getPos(), SoundEvents.ITEM_GLOW_INK_SAC_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            // Cursed fix for the "Glow and Behold!" Advancement
            Criteria.ITEM_USED_ON_BLOCK.trigger((ServerPlayerEntity) player, signBlockEntity.getPos(), Items.GLOW_INK_SAC.getDefaultStack());
            return true;
        } else {
            return false;
        }
    }
}
