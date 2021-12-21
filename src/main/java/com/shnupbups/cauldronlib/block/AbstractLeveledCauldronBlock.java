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
		return (double) getFluidLevel(state) / ((double) getMaxLevel() / 3d);
	}

	/**
	 * Gets the fluid level of this cauldron.
	 *
	 * @param state the block state of this cauldron
	 */
	public int getFluidLevel(BlockState state) {
		return state.get(getLevelProperty());
	}

	/**
	 * Sets the fluid level of this cauldron.
	 *
	 * @param state    the block state of this cauldron
	 * @param world    the world this cauldron is in
	 * @param pos      the position of this cauldron
	 * @param required whether the cauldron is required to be able to hold the exact amount given
	 * @param level    the amount to set the fluid level to
	 * @return whether any change was made
	 */
	public boolean setFluidLevel(BlockState state, World world, BlockPos pos, boolean required, int level) {
		if (required && (level < 0 || level > getMaxLevel())) return false;
		level = Math.min(level, getMaxLevel());
		if (getFluidLevel(state) == level) return false;
		return world.setBlockState(pos, level <= 0 ? Blocks.CAULDRON.getDefaultState() : state.with(getLevelProperty(), level));
	}

	/**
	 * Decrements the fluid level of this cauldron.
	 *
	 * @param state    the block state of this cauldron
	 * @param world    the world this cauldron is in
	 * @param pos      the position of this cauldron
	 * @param required whether the cauldron is required to have the amount of fluid to decrement in the first place
	 * @param amount   the amount to decrement the fluid level by
	 * @return whether any change was made
	 */
	public boolean decrementFluidLevel(BlockState state, World world, BlockPos pos, boolean required, int amount) {
		int level = getFluidLevel(state) - amount;
		return setFluidLevel(state, world, pos, required, level);
	}

	/**
	 * Decrements the fluid level of this cauldron by 1.
	 *
	 * @param state    the block state of this cauldron
	 * @param world    the world this cauldron is in
	 * @param pos      the position of this cauldron
	 * @param required whether the cauldron is required to have the amount of fluid to decrement in the first place
	 * @return whether any change was made
	 */
	public boolean decrementFluidLevel(BlockState state, World world, BlockPos pos, boolean required) {
		return decrementFluidLevel(state, world, pos, required, 1);
	}

	/**
	 * Increments the fluid level of this cauldron.
	 *
	 * @param state    the block state of this cauldron
	 * @param world    the world this cauldron is in
	 * @param pos      the position of this cauldron
	 * @param required whether the cauldron is required to have the space for fluid to increment in the first place
	 * @param amount   the amount to increment the fluid level by
	 * @return whether any change was made
	 */
	public boolean incrementFluidLevel(BlockState state, World world, BlockPos pos, boolean required, int amount) {
		int level = getFluidLevel(state) + amount;
		return setFluidLevel(state, world, pos, required, level);
	}

	/**
	 * Increments the fluid level of this cauldron by 1.
	 *
	 * @param state    the block state of this cauldron
	 * @param world    the world this cauldron is in
	 * @param pos      the position of this cauldron
	 * @param required whether the cauldron is required to have the space for fluid to increment in the first place
	 * @return whether any change was made
	 */
	public boolean incrementFluidLevel(BlockState state, World world, BlockPos pos, boolean required) {
		return incrementFluidLevel(state, world, pos, required, 1);
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
