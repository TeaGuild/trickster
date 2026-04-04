package dev.enjarai.trickster.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class LightSconceBlock extends LightBlock implements Waterloggable, Stainable {
    public static final DirectionProperty FACING = Properties.FACING;
    public static final BooleanProperty HAS_LIGHT = BooleanProperty.of("has_light");

    public static final VoxelShape[] SHAPES = new VoxelShape[] {
        createCuboidShape(5, 5, 5, 11, 16, 11), //down
        createCuboidShape(5, 0, 5, 11, 11, 11), //up
        createCuboidShape(5, 5, 5, 11, 11, 16), //north
        createCuboidShape(5, 5, 0, 11, 11, 11), //south
        createCuboidShape(5, 5, 5, 16, 11, 11), //west
        createCuboidShape(0, 5, 5, 11, 11, 11)  //east
    };

    protected LightSconceBlock() {
        super(Settings.create()
            .noCollision().luminance((state) -> state.get(HAS_LIGHT) ? STATE_TO_LUMINANCE.applyAsInt(state) : 0)
            .sounds(BlockSoundGroup.METAL)
            .pistonBehavior(PistonBehavior.DESTROY).hardness(3.5f));
        setDefaultState(getStateManager().getDefaultState()
            .with(LIGHT_LEVEL, 0)
            .with(WATERLOGGED, false)
            .with(FACING, Direction.UP)
            .with(HAS_LIGHT, false)
        );
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING).add(HAS_LIGHT);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES[state.get(FACING).getId()];
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!state.get(HAS_LIGHT)) return;
        super.randomDisplayTick(state, world, pos, random);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        var state = super.getPlacementState(ctx);
        var oldState = ctx.getWorld().getBlockState(ctx.getBlockPos());
        if (oldState.getBlock() == ModBlocks.LIGHT) {
            state = state.with(LIGHT_LEVEL, oldState.get(LIGHT_LEVEL)).with(HAS_LIGHT, true);
        }
        return state.with(FACING, ctx.getSide());
    }



    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        super.onBroken(world, pos, state);
        if (state.get(HAS_LIGHT)) {
            world.setBlockState(pos, ModBlocks.LIGHT.getDefaultState().with(LIGHT_LEVEL, state.get(LIGHT_LEVEL)), 0);

        }
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return MapCodec.unit(ModBlocks.LIGHT_SCONCE);
    }
}
