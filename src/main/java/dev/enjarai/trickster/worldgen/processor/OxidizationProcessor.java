package dev.enjarai.trickster.worldgen.processor;

import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.CodecUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;

public class OxidizationProcessor extends StructureProcessor {
    public static final StructEndec<OxidizationProcessor> ENDEC = StructEndecBuilder.of(
        CodecUtils.toEndec(Oxidizable.OxidationLevel.CODEC).optionalFieldOf("min", o -> o.min, Oxidizable.OxidationLevel.UNAFFECTED),
        CodecUtils.toEndec(Oxidizable.OxidationLevel.CODEC).optionalFieldOf("max", o -> o.max, Oxidizable.OxidationLevel.OXIDIZED),
        OxidizationProcessor::new
    );

    private final Oxidizable.OxidationLevel min;
    private final Oxidizable.OxidationLevel max;

    public OxidizationProcessor(Oxidizable.OxidationLevel min, Oxidizable.OxidationLevel max) {
        this.min = min;
        this.max = max;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return ModProcessorTypes.OXIDIZE;
    }

    public StructureTemplate.StructureBlockInfo process(
        WorldView world,
        BlockPos pos,
        BlockPos pivot,
        StructureTemplate.StructureBlockInfo originalBlockInfo,
        StructureTemplate.StructureBlockInfo currentBlockInfo,
        StructurePlacementData data
    ) {
        return new StructureTemplate.StructureBlockInfo(
            currentBlockInfo.pos(),
            oxidize(data.getRandom(currentBlockInfo.pos()), currentBlockInfo.state()),
            currentBlockInfo.nbt()
        );
    }

    private BlockState oxidize(Random random, BlockState state) {
        while (
            state.getBlock() instanceof Oxidizable block
                && block.getDegradationLevel().ordinal() < max.ordinal()
                && (block.getDegradationLevel().ordinal() < min.ordinal() || random.nextBoolean())
        ) {
            state = block.getDegradationResult(state).orElse(state);
        }

        return state;
    }
}
