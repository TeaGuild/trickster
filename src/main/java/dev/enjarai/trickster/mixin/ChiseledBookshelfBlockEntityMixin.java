package dev.enjarai.trickster.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerLootComponent;
import net.minecraft.inventory.LootableInventory;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChiseledBookshelfBlockEntity.class)
public abstract class ChiseledBookshelfBlockEntityMixin extends BlockEntity implements LootableInventory {
    public ChiseledBookshelfBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Unique
    @Nullable
    protected RegistryKey<LootTable> lootTable;
    @Unique
    protected long lootTableSeed = 0L;

    @Override
    public @Nullable RegistryKey<LootTable> getLootTable() {
        return lootTable;
    }

    @Override
    public void setLootTable(@Nullable RegistryKey<LootTable> lootTable) {
        this.lootTable = lootTable;
    }

    @Override
    public long getLootTableSeed() {
        return lootTableSeed;
    }

    @Override
    public void setLootTableSeed(long lootTableSeed) {
        this.lootTableSeed = lootTableSeed;
    }

    @WrapMethod(method = "readComponents")
    public void readLootComponent(ComponentsAccess components, Operation<Void> original) {
        original.call(components);
        ContainerLootComponent containerLootComponent = components.get(DataComponentTypes.CONTAINER_LOOT);
        if (containerLootComponent != null) {
            this.lootTable = containerLootComponent.lootTable();
            this.lootTableSeed = containerLootComponent.seed();
            this.generateLoot(null);
        }
    }

    @WrapMethod(method = "addComponents")
    public void addLootComponent(ComponentMap.Builder componentMapBuilder, Operation<Void> original) {
        original.call(componentMapBuilder);
        if (this.lootTable != null) {
            componentMapBuilder.add(DataComponentTypes.CONTAINER_LOOT, new ContainerLootComponent(this.lootTable, this.lootTableSeed));
        }
    }

    @WrapMethod(method = "readNbt")
    protected void readLootNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, Operation<Void> original) {
        original.call(nbt, registryLookup);
        readLootTable(nbt);
        this.generateLoot(null);
    }

    @WrapMethod(method = "writeNbt")
    protected void writeLootNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, Operation<Void> original) {
        original.call(nbt, registryLookup);
        writeLootTable(nbt);
    }
}
