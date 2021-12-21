package com.shnupbups.cauldronlib.block;

import java.util.Map;

import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.item.Item;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;

/**
 * Represents a cauldron that has 3 fluid levels, like vanilla Water Cauldrons.
 */
public class ThreeLeveledCauldronBlock extends AbstractLeveledCauldronBlock {
	public static final IntProperty LEVEL = Properties.LEVEL_3;

	public ThreeLeveledCauldronBlock(Settings settings, Map<Item, CauldronBehavior> behaviorMap) {
		super(settings, behaviorMap);
	}

	@Override
	public IntProperty getLevelProperty() {
		return LEVEL;
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}
}
