package com.shnupbups.cauldronlib.block;

import java.util.Map;

import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.shnupbups.cauldronlib.CauldronLib;

/**
 * Represents a cauldron that has multiple fluid levels, like vanilla Water Cauldrons.
 */
public abstract class AbstractLeveledCauldronBlock extends AbstractCauldronBlock {
	public AbstractLeveledCauldronBlock(Settings settings, Map<Item, CauldronBehavior> behaviorMap) {
		super(settings, behaviorMap);
		this.setDefaultState(this.stateManager.getDefaultState().with(getLevelProperty(), 1));
		CauldronLib.registerBehaviorMap(behaviorMap);
	}

	/**
	 * Gets the block state property that represents the fluid level of this cauldron.
	 */
	public abstract IntProperty getLevelProperty();

	/**
	 * Gets the maximum fluid level of this cauldron.
	 */
	public abstract int getMaxLevel();

	private double getLevelAsThirds(BlockState state) {
		return (double) state.get(getLevelProperty()) / ((double) getMaxLevel() / 3d);
	}

	/**
	 * Decrements the fluid level of this cauldron.
	 *
	 * @param state  the block state of this cauldron
	 * @param world  the world
	 * @param pos    the position of the cauldron
	 * @param amount the amount to decrement the fluid level by
	 */
	public void decrementFluidLevel(BlockState state, World world, BlockPos pos, int amount) {
		int level = Math.min(state.get(getLevelProperty()) - amount, getMaxLevel());
		world.setBlockState(pos, level <= 0 ? Blocks.CAULDRON.getDefaultState() : state.with(getLevelProperty(), level));
	}

	/**
	 * Decrements the fluid level of this cauldron by 1.
	 *
	 * @param state the block state of this cauldron
	 * @param world the world
	 * @param pos   the position of the cauldron
	 */
	public void decrementFluidLevel(BlockState state, World world, BlockPos pos) {
		decrementFluidLevel(state, world, pos, 1);
	}

	/**
	 * Increments the fluid level of this cauldron.
	 *
	 * @param state  the block state of this cauldron
	 * @param world  the world
	 * @param pos    the position of the cauldron
	 * @param amount the amount to increment the fluid level by
	 */
	public void incrementFluidLevel(BlockState state, World world, BlockPos pos, int amount) {
		int level = Math.min(state.get(getLevelProperty()) + amount, getMaxLevel());
		world.setBlockState(pos, level <= 0 ? Blocks.CAULDRON.getDefaultState() : state.with(getLevelProperty(), level));
	}

	/**
	 * Increments the fluid level of this cauldron by 1.
	 *
	 * @param state the block state of this cauldron
	 * @param world the world
	 * @param pos   the position of the cauldron
	 */
	public void incrementFluidLevel(BlockState state, World world, BlockPos pos) {
		incrementFluidLevel(state, world, pos, 1);
	}

	@Override
	public boolean isFull(BlockState state) {
		return state.get(getLevelProperty()) == getMaxLevel();
	}

	@Override
	protected double getFluidHeight(BlockState state) {
		return (6.0D + getLevelAsThirds(state) * 3.0D) / 16.0D;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return (int) Math.max(1, Math.ceil(getLevelAsThirds(state)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(getLevelProperty());
	}
}
