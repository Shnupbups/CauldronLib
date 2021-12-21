package com.shnupbups.cauldronlib;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.Block;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

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
	 * Creates a new cauldron behavior for filling a cauldron from an item.
	 *
	 * <p>Note that filling in this context means simply putting the smallest fluid level possible into the cauldron,
	 * so cauldrons with multiple fluid levels will only have a level of 1 from this behavior.
	 *
	 * <p>If that is not the desired effect, then create a custom behavior.
	 *
	 * @param cauldron the filled cauldron that results
	 * @param itemEmptySound the sound event for emptying the item
	 */
	public static CauldronBehavior createFillBehavior(Block cauldron, SoundEvent itemEmptySound) {
		return (state, world, pos, player, hand, stack) -> CauldronBehavior.fillCauldron(world, pos, player, hand, stack, cauldron.getDefaultState(), itemEmptySound);
	}

	/**
	 * Registers a new global cauldron behavior for filling a cauldron from an item.
	 *
	 * <p>Note that filling in this context means simply putting the smallest fluid level possible into the cauldron,
	 * so cauldrons with multiple fluid levels will only have a level of 1 from this behavior.
	 *
	 * <p>If that is not the desired effect, then create and register a custom behavior.
	 *
	 * @param item   the item to fill from
	 * @param cauldron the filled cauldron that results
	 * @param itemEmptySound the sound event for emptying the item
	 */
	public static void registerFillBehavior(Item item, Block cauldron, SoundEvent itemEmptySound) {
		registerGlobalBehavior(item, createFillBehavior(cauldron, itemEmptySound));
	}

	/**
	 * Creates a new cauldron behavior for filling a cauldron from a bucket.
	 *
	 * <p>Note that filling in this context means simply putting the smallest fluid level possible into the cauldron,
	 * so cauldrons with multiple fluid levels will only have a level of 1 from this behavior.
	 *
	 * <p>If that is not the desired effect, then create a custom behavior.
	 *
	 * @param cauldron the filled cauldron that results
	 */
	public static CauldronBehavior createFillFromBucketBehavior(Block cauldron) {
		return createFillBehavior(cauldron, SoundEvents.ITEM_BUCKET_EMPTY);
	}

	/**
	 * Registers a new global cauldron behavior for filling a cauldron from an item.
	 *
	 * <p>Note that filling in this context means simply putting the smallest fluid level possible into the cauldron,
	 * so cauldrons with multiple fluid levels will only have a level of 1 from this behavior.
	 *
	 * <p>If that is not the desired effect, then create and register a custom behavior.
	 *
	 * @param bucket   the bucket to fill from
	 * @param cauldron the filled cauldron that results
	 */
	public static void registerFillFromBucketBehavior(Item bucket, Block cauldron) {
		registerGlobalBehavior(bucket, createFillFromBucketBehavior(cauldron));
	}

	/**
	 * Creates a new cauldron behavior for emptying a cauldron <b>entirely</b> into an item.
	 *
	 * @param item the item that results
	 */
	public static CauldronBehavior createEmptyBehavior(Item item, SoundEvent itemFillSound) {
		return (state2, world, pos, player, hand, stack) -> CauldronBehavior.emptyCauldron(state2, world, pos, player, hand, stack, new ItemStack(item), state -> ((AbstractCauldronBlock)state.getBlock()).isFull(state), itemFillSound);
	}

	/**
	 * Creates a new cauldron behavior for emptying a cauldron <b>entirely</b> into a bucket.
	 *
	 * @param bucket the bucket that results
	 */
	public static CauldronBehavior createEmptyIntoBucketBehavior(Item bucket) {
		return createEmptyBehavior(bucket, SoundEvents.ITEM_BUCKET_FILL);
	}

	/**
	 * A pair of an item and a cauldron behavior
	 */
	public record CauldronBehaviorMapEntry(Item item, CauldronBehavior behavior) {
	}
}
