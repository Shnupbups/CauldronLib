package com.shnupbups.cauldronlib;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import com.shnupbups.cauldronlib.block.AbstractLeveledCauldronBlock;
import com.shnupbups.cauldronlib.block.FullCauldronBlock;

public class CauldronLib {
	private static final Set<Map<Item, CauldronBehavior>> CAULDRON_BEHAVIOR_MAPS = new HashSet<>(Set.of(
			CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR,
			CauldronBehavior.WATER_CAULDRON_BEHAVIOR,
			CauldronBehavior.LAVA_CAULDRON_BEHAVIOR,
			CauldronBehavior.POWDER_SNOW_CAULDRON_BEHAVIOR
	));

	private static final Set<CauldronBehaviorMapEntry> GLOBAL_BEHAVIORS = new HashSet<>();

	/**
	 * Registers a cauldron behavior map.
	 *
	 * <p>Unnecessary if already parsed to {@link AbstractLeveledCauldronBlock} or {@link FullCauldronBlock}.
	 *
	 * @param behaviorMap the behavior map to register
	 */
	public static void registerBehaviorMap(Map<Item, CauldronBehavior> behaviorMap) {
		CAULDRON_BEHAVIOR_MAPS.add(behaviorMap);
		addGlobalBehaviors(behaviorMap);
	}

	/**
	 * Gets all registered cauldron behavior maps.
	 */
	public static Set<Map<Item, CauldronBehavior>> getCauldronBehaviorMaps() {
		return CAULDRON_BEHAVIOR_MAPS;
	}

	/**
	 * Gets all registered global cauldron behaviors.
	 */
	public static Set<CauldronBehaviorMapEntry> getGlobalBehaviors() {
		return GLOBAL_BEHAVIORS;
	}

	/**
	 * Adds all currently registered global behaviors to the given behavior map.
	 *
	 * @param behaviorMap the behavior map to add to
	 */
	private static void addGlobalBehaviors(Map<Item, CauldronBehavior> behaviorMap) {
		CauldronBehavior.registerBucketBehavior(behaviorMap);
		getGlobalBehaviors().forEach((behavior) -> {
			behaviorMap.put(behavior.item(), behavior.behavior());
		});
	}

	/**
	 * Registers a new global cauldron behavior.
	 *
	 * <p>Global cauldron behaviors are added to every registered cauldron behavior map.
	 */
	public static void registerGlobalBehavior(Item item, CauldronBehavior behavior) {
		registerGlobalBehavior(new CauldronBehaviorMapEntry(item, behavior));
	}

	/**
	 * Registers new global cauldron behaviors.
	 *
	 * <p>Global cauldron behaviors are added to every registered cauldron behavior map.
	 */
	public static void registerGlobalBehavior(CauldronBehaviorMapEntry... behaviors) {
		Arrays.stream(behaviors).forEach((behavior -> {
			getCauldronBehaviorMaps().forEach((map) -> map.put(behavior.item(), behavior.behavior));
			getGlobalBehaviors().add(behavior);
		}));
	}

	/**
	 * Creates a new cauldron behavior for filling a cauldron from a bucket.
	 *
	 * <p>Note that filling in this context means simply putting the smallest fluid level possible into the cauldron,
	 * so cauldron types with multiple fluid levels will only have a level of 1 from this behavior.
	 *
	 * <p>If that is not the desired effect, then create a custom behavior.
	 *
	 * @param cauldron         the filled cauldron that results
	 * @param bucketEmptySound the sound event for emptying the bucket
	 */
	public static CauldronBehavior createFillFromBucketBehavior(Block cauldron, SoundEvent bucketEmptySound) {
		return (state, world, pos, player, hand, stack) -> CauldronBehavior.fillCauldron(world, pos, player, hand, stack, cauldron.getDefaultState(), bucketEmptySound);
	}

	/**
	 * Registers a new global cauldron behavior for filling a cauldron from a bucket.
	 *
	 * <p>Note that filling in this context means simply putting the smallest fluid level possible into the cauldron,
	 * so cauldron types with multiple fluid levels will only have a level of 1 from this behavior.
	 *
	 * <p>If that is not the desired effect, then create and register a custom behavior.
	 *
	 * @param bucket           the bucket to fill the cauldron from
	 * @param cauldron         the filled cauldron that results
	 * @param bucketEmptySound the sound event for emptying the bucket
	 */
	public static void registerFillFromBucketBehavior(Item bucket, Block cauldron, SoundEvent bucketEmptySound) {
		registerGlobalBehavior(bucket, createFillFromBucketBehavior(cauldron, bucketEmptySound));
	}

	/**
	 * Creates a new cauldron behavior for filling a cauldron from a bucket.
	 *
	 * <p>Note that filling in this context means simply putting the smallest fluid level possible into the cauldron,
	 * so cauldron types with multiple fluid levels will only have a level of 1 from this behavior.
	 *
	 * <p>If that is not the desired effect, then create a custom behavior.
	 *
	 * @param cauldron the filled cauldron that results
	 */
	public static CauldronBehavior createFillFromBucketBehavior(Block cauldron) {
		return createFillFromBucketBehavior(cauldron, SoundEvents.ITEM_BUCKET_EMPTY);
	}

	/**
	 * Registers a new global cauldron behavior for filling a cauldron from a bucket.
	 *
	 * <p>Note that filling in this context means simply putting the smallest fluid level possible into the cauldron,
	 * so cauldron types with multiple fluid levels will only have a level of 1 from this behavior.
	 *
	 * <p>If that is not the desired effect, then create and register a custom behavior.
	 *
	 * @param bucket   the bucket to fill the cauldron from
	 * @param cauldron the filled cauldron that results
	 */
	public static void registerFillFromBucketBehavior(Item bucket, Block cauldron) {
		registerGlobalBehavior(bucket, createFillFromBucketBehavior(cauldron));
	}

	/**
	 * Creates a new cauldron behavior for filling a cauldron from a bottle.
	 *
	 * <p>Note that filling in this context means simply putting the smallest fluid level possible into the cauldron,
	 * so cauldron types with multiple fluid levels will only have a level of 1 from this behavior.
	 *
	 * <p>If that is not the desired effect, then create a custom behavior.
	 *
	 * @param cauldron         the filled cauldron that results
	 * @param bottleEmptySound the sound event for emptying the bottle
	 */
	public static CauldronBehavior createFillFromBottleBehavior(Block cauldron, SoundEvent bottleEmptySound) {
		return (state, world, pos, player, hand, stack) -> {
			if (!world.isClient) {
				Item item = stack.getItem();
				player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
				player.incrementStat(Stats.USE_CAULDRON);
				player.incrementStat(Stats.USED.getOrCreateStat(item));
				world.setBlockState(pos, cauldron.getDefaultState());
				world.playSound(null, pos, bottleEmptySound, SoundCategory.BLOCKS, 1.0F, 1.0F);
				world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
			}

			return ActionResult.success(world.isClient);
		};
	}

	/**
	 * Creates a new cauldron behavior for filling a cauldron from a bottle.
	 *
	 * <p>Note that filling in this context means simply putting the smallest fluid level possible into the cauldron,
	 * so cauldron types with multiple fluid levels will only have a level of 1 from this behavior.
	 *
	 * <p>If that is not the desired effect, then create a custom behavior.
	 *
	 * @param cauldron the filled cauldron that results
	 */
	public static CauldronBehavior createFillFromBottleBehavior(Block cauldron) {
		return createFillFromBottleBehavior(cauldron, SoundEvents.ITEM_BOTTLE_EMPTY);
	}

	/**
	 * Creates a new cauldron behavior for emptying a <b>full</b> cauldron <b>entirely</b> into an item.
	 *
	 * @param item the item that results
	 */
	public static CauldronBehavior createEmptyBehavior(Item item, SoundEvent itemFillSound) {
		return (state, world, pos, player, hand, stack) -> CauldronBehavior.emptyCauldron(state, world, pos, player, hand, stack, new ItemStack(item), state2 -> ((AbstractCauldronBlock) state2.getBlock()).isFull(state2), itemFillSound);
	}

	/**
	 * Creates a new cauldron behavior for emptying a <b>full</b> cauldron <b>entirely</b> into a bucket.
	 *
	 * @param bucket the bucket that results
	 */
	public static CauldronBehavior createEmptyIntoBucketBehavior(Item bucket) {
		return createEmptyBehavior(bucket, SoundEvents.ITEM_BUCKET_FILL);
	}

	/**
	 * Sets the fluid level of a cauldron.
	 *
	 * @param state    the block state of the cauldron
	 * @param world    the world the cauldron is in
	 * @param pos      the position of the cauldron
	 * @param required whether the cauldron is required to be able to hold the exact amount given
	 * @param level    the amount to set the fluid level to
	 * @return whether any change was made
	 */
	public static boolean setFluidLevel(BlockState state, World world, BlockPos pos, boolean required, int level) {
		int maxLevel = getMaxFluidLevel(state);
		int actualLevel = Math.max(0, Math.min(level, maxLevel));

		if (maxLevel == -1 || (level != actualLevel && required) || getFluidLevel(state) == actualLevel) return false;
		
		if (state.getBlock() instanceof AbstractLeveledCauldronBlock block) {
			return block.setFluidLevel(state, world, pos, required, actualLevel);
		} else if ((state.getBlock() instanceof FullCauldronBlock || state.isOf(Blocks.LAVA_CAULDRON)) && actualLevel == 0) {
			return world.setBlockState(pos, Blocks.CAULDRON.getDefaultState());
		} else if (state.getBlock() instanceof LeveledCauldronBlock) {
			return world.setBlockState(pos, actualLevel == 0 ? Blocks.CAULDRON.getDefaultState() : state.with(LeveledCauldronBlock.LEVEL, actualLevel));
		}

		return false;
	}

	/**
	 * Sets the fluid level of a cauldron.
	 *
	 * @param state the block state of the cauldron
	 * @param world the world the cauldron is in
	 * @param pos   the position of the cauldron
	 * @param level the amount to set the fluid level to
	 * @return whether any change was made
	 */
	public static boolean setFluidLevel(BlockState state, World world, BlockPos pos, int level) {
		return setFluidLevel(state, world, pos, true, level);
	}

	/**
	 * Decrements the fluid level of a cauldron.
	 *
	 * @param state    the block state of the cauldron
	 * @param world    the world the cauldron is in
	 * @param pos      the position of the cauldron
	 * @param required whether the cauldron is required to have the amount of fluid to decrement in the first place
	 * @param amount   the amount to decrement the fluid level by
	 * @return whether any change was made
	 */
	public static boolean decrementFluidLevel(BlockState state, World world, BlockPos pos, boolean required, int amount) {
		return setFluidLevel(state, world, pos, required, getFluidLevel(state) - amount);
	}

	/**
	 * Increments the fluid level of a cauldron.
	 *
	 * @param state    the block state of the cauldron
	 * @param world    the world the cauldron is in
	 * @param pos      the position of the cauldron
	 * @param required whether the cauldron is required to have the space for fluid to increment in the first place
	 * @param amount   the amount to increment the fluid level by
	 * @return whether any change was made
	 */
	public static boolean incrementFluidLevel(BlockState state, World world, BlockPos pos, boolean required, int amount) {
		return setFluidLevel(state, world, pos, required, getFluidLevel(state) + amount);
	}

	/**
	 * Decrements the fluid level of a cauldron by 1.
	 *
	 * @param state    the block state of the cauldron
	 * @param world    the world the cauldron is in
	 * @param pos      the position of the cauldron
	 * @param required whether the cauldron is required to have the amount of fluid to decrement in the first place
	 * @return whether any change was made
	 */
	public static boolean decrementFluidLevel(BlockState state, World world, BlockPos pos, boolean required) {
		return decrementFluidLevel(state, world, pos, required, 1);
	}

	/**
	 * Increments the fluid level of a cauldron by 1.
	 *
	 * @param state    the block state of the cauldron
	 * @param world    the world the cauldron is in
	 * @param pos      the position of the cauldron
	 * @param required whether the cauldron is required to have the space for fluid to increment in the first place
	 * @return whether any change was made
	 */
	public static boolean incrementFluidLevel(BlockState state, World world, BlockPos pos, boolean required) {
		return incrementFluidLevel(state, world, pos, required, 1);
	}

	/**
	 * Decrements the fluid level of a cauldron.
	 *
	 * @param state  the block state of the cauldron
	 * @param world  the world the cauldron is in
	 * @param pos    the position of the cauldron
	 * @param amount the amount to decrement the fluid level by
	 * @return whether any change was made
	 */
	public static boolean decrementFluidLevel(BlockState state, World world, BlockPos pos, int amount) {
		return decrementFluidLevel(state, world, pos, true, amount);
	}

	/**
	 * Increments the fluid level of a cauldron.
	 *
	 * @param state  the block state of the cauldron
	 * @param world  the world the cauldron is in
	 * @param pos    the position of the cauldron
	 * @param amount the amount to increment the fluid level by
	 * @return whether any change was made
	 */
	public static boolean incrementFluidLevel(BlockState state, World world, BlockPos pos, int amount) {
		return incrementFluidLevel(state, world, pos, true, amount);
	}

	/**
	 * Decrements the fluid level of a cauldron by 1.
	 *
	 * @param state the block state of the cauldron
	 * @param world the world the cauldron is in
	 * @param pos   the position of the cauldron
	 * @return whether any change was made
	 */
	public static boolean decrementFluidLevel(BlockState state, World world, BlockPos pos) {
		return decrementFluidLevel(state, world, pos, true, 1);
	}

	/**
	 * Increments the fluid level of a cauldron by 1.
	 *
	 * @param state the block state of the cauldron
	 * @param world the world the cauldron is in
	 * @param pos   the position of the cauldron
	 * @return whether any change was made
	 */
	public static boolean incrementFluidLevel(BlockState state, World world, BlockPos pos) {
		return incrementFluidLevel(state, world, pos, true, 1);
	}

	/**
	 * Gets the fluid level of a cauldron, or {@code -1} if not a known cauldron.
	 *
	 * @param state the block state of the cauldron
	 */
	public static int getFluidLevel(BlockState state) {
		if (state.getBlock() instanceof AbstractLeveledCauldronBlock block) {
			return block.getFluidLevel(state);
		} else if (state.getBlock() instanceof FullCauldronBlock || state.isOf(Blocks.LAVA_CAULDRON)) {
			return 1;
		} else if (state.getBlock() instanceof LeveledCauldronBlock) {
			return state.get(LeveledCauldronBlock.LEVEL);
		} else if (state.isOf(Blocks.CAULDRON)) {
			return 0;
		}
		return -1;
	}

	/**
	 * Gets the maximum fluid level of a cauldron, or {@code -1} if not a known cauldron.
	 *
	 * @param state the block state of the cauldron
	 */
	public static int getMaxFluidLevel(BlockState state) {
		if (state.getBlock() instanceof AbstractLeveledCauldronBlock block) {
			return block.getMaxLevel();
		} else if (state.getBlock() instanceof FullCauldronBlock || state.isOf(Blocks.LAVA_CAULDRON)) {
			return 1;
		} else if (state.getBlock() instanceof LeveledCauldronBlock) {
			return 3;
		} else if (state.isOf(Blocks.CAULDRON)) {
			return 0;
		}
		return -1;
	}

	/**
	 * A pair of an item and a cauldron behavior.
	 */
	public record CauldronBehaviorMapEntry(Item item, CauldronBehavior behavior) {
	}
}
