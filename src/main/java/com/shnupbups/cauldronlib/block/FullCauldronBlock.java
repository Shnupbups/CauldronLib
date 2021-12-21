package com.shnupbups.cauldronlib.block;

import java.util.Map;

import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.shnupbups.cauldronlib.CauldronLib;

/**
 * Represents a cauldron that only has a single fluid level, like vanilla Lava Cauldrons.
 */
public class FullCauldronBlock extends AbstractCauldronBlock {
	public FullCauldronBlock(Settings settings, Map<Item, CauldronBehavior> behaviorMap) {
		super(settings, behaviorMap);
		CauldronLib.registerBehaviorMap(behaviorMap);
	}

	@Override
	protected double getFluidHeight(BlockState state) {
		return 0.9375D;
	}

	@Override
	public boolean isFull(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return 3;
	}
}
