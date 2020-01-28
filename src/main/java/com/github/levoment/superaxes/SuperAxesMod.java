package com.github.levoment.superaxes;

import com.github.levoment.superaxes.Items.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;


public class SuperAxesMod implements ModInitializer {

	public static String MODID = "lvmnt";
	public static final ItemGroup SUPERAXES_GROUP = FabricItemGroupBuilder.build(new Identifier(MODID, "superaxes"), () -> new ItemStack(ModItems.WoodenSuperAxe));

	@Override
	public void onInitialize() {

		// Initialize items
		ModItems.initializeItems();

		// Populate the list of the mod's items and their identifiers
        ModItems.poplateMapOfIdentifiers();

		// Register the items
		ModItems.registerItems();
	}
}
