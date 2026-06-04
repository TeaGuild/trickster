package dev.enjarai.trickster.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;

public class ColorBlock extends BlockWithEntity {
    protected ColorBlock() {
        super(Settings.create().hardness(0.2f).resistance(0.2f).sounds(BlockSoundGroup.DECORATED_POT));
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return MapCodec.unit(ModBlocks.COLOR_BLOCK);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ColorBlockEntity(pos, state);
    }

    public static int getColor(int[] colors, float t) {
        var colA = colors[((int) t) % colors.length];
        var colB = colors[((int) t + 1) % colors.length];
        return ColorHelper.Argb.lerp(t % 1, colA, colB);
    }
}
