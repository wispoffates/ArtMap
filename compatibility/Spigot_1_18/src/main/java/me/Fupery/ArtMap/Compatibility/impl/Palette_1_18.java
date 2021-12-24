package me.Fupery.ArtMap.Compatibility.impl;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.Fupery.ArtMap.api.Colour.ArtDye;
import me.Fupery.ArtMap.api.Colour.BasicDye;
import me.Fupery.ArtMap.api.Colour.DyeType;
import me.Fupery.ArtMap.api.Colour.Palette;
import me.Fupery.ArtMap.api.Colour.ShadingDye;
import me.Fupery.ArtMap.api.Config.Lang;

public class Palette_1_18 implements Palette {
	public final ArtDye

			VOID  				= new BasicDye(Lang.DYE_VOID.get(), "VOID", 0, ChatColor.DARK_GREEN, Material.ENDER_EYE),
	        GRASS 				= new BasicDye(Lang.DYE_GRASS.get(), "GRASS", 1, ChatColor.DARK_GREEN, Material.GRASS),
			CREAM 				= new BasicDye(Lang.DYE_CREAM.get(), "CREAM", 2, ChatColor.GOLD, Material.PUMPKIN_SEEDS),
	        LIGHT_GRAY 			= new BasicDye(Lang.DYE_LIGHT_GRAY.get(), "LIGHT_GRAY", 3, ChatColor.GRAY, Material.COBWEB),															
	        RED 				= new BasicDye(Lang.DYE_RED.get(), "RED", 4, ChatColor.RED, Material.RED_DYE),
			ICE 				= new BasicDye(Lang.DYE_ICE.get(), "ICE", 5, ChatColor.GRAY, Material.ICE), 
	        SILVER 				= new BasicDye(Lang.DYE_SILVER.get(), "SILVER", 6, ChatColor.GRAY, Material.LIGHT_GRAY_DYE),
	        LEAVES 				= new BasicDye(Lang.DYE_LEAVES.get(), "LEAVES", 7, ChatColor.GREEN, Material.OAK_LEAVES),																
			SNOW 				= new BasicDye(Lang.DYE_SNOW.get(), "SNOW", 8, ChatColor.BLUE, Material.SNOW), 
	        GRAY 				= new BasicDye(Lang.DYE_GRAY.get(), "GRAY", 9, ChatColor.DARK_GRAY, Material.GRAY_DYE),
			COFFEE 				= new BasicDye(Lang.DYE_COFFEE.get(), "COFFEE", 10, ChatColor.DARK_RED, Material.MELON_SEEDS),
			STONE 				= new BasicDye(Lang.DYE_STONE.get(), "STONE", 11, ChatColor.DARK_GRAY, Material.GHAST_TEAR), 
			WATER 				= new BasicDye(Lang.DYE_WATER.get(), "WATER", 12, ChatColor.DARK_BLUE, Material.LAPIS_BLOCK), 
	        DARK_WOOD			= new BasicDye(Lang.DYE_DARK_WOOD.get(), "DARK_WOOD", 13, ChatColor.GREEN, Material.DARK_OAK_LOG),															
			WHITE 				= new BasicDye(Lang.DYE_WHITE.get(), "WHITE", 14, ChatColor.WHITE, Material.BONE_MEAL),
	        ORANGE 				= new BasicDye(Lang.DYE_ORANGE.get(), "ORANGE", 15, ChatColor.GOLD, Material.ORANGE_DYE),
	        MAGENTA 			= new BasicDye(Lang.DYE_MAGENTA.get(), "MAGENTA", 16, ChatColor.LIGHT_PURPLE, Material.MAGENTA_DYE),
	        LIGHT_BLUE 			= new BasicDye(Lang.DYE_LIGHT_BLUE.get(), "LIGHT_BLUE", 17, ChatColor.BLUE, Material.LIGHT_BLUE_DYE),
	        YELLOW 				= new BasicDye(Lang.DYE_YELLOW.get(), "YELLOW", 18, ChatColor.YELLOW, Material.YELLOW_DYE),
	        LIME 				= new BasicDye(Lang.DYE_LIME.get(), "LIME", 19, ChatColor.GREEN, Material.LIME_DYE),
	        PINK 				= new BasicDye(Lang.DYE_PINK.get(), "PINK", 20, ChatColor.LIGHT_PURPLE, Material.PINK_DYE),
			GRAPHITE 			= new BasicDye(Lang.DYE_GRAPHITE.get(), "GRAPHITE", 21, ChatColor.DARK_GRAY, Material.FLINT),
	        GUNPOWDER			= new BasicDye(Lang.DYE_GUNPOWDER.get(), "GUNPOWDER", 22, ChatColor.GRAY, Material.GUNPOWDER),
	        CYAN 				= new BasicDye(Lang.DYE_CYAN.get(), "CYAN", 23, ChatColor.DARK_AQUA, Material.CYAN_DYE),
	        PURPLE 				= new BasicDye(Lang.DYE_PURPLE.get(), "PURPLE", 24, ChatColor.DARK_PURPLE, Material.PURPLE_DYE),
	        BLUE 				= new BasicDye(Lang.DYE_BLUE.get(), "BLUE", 25, ChatColor.BLUE, Material.LAPIS_LAZULI),
	        BROWN 				= new BasicDye(Lang.DYE_BROWN.get(), "BROWN", 26, ChatColor.DARK_RED, Material.COCOA_BEANS),
	        GREEN 				= new BasicDye(Lang.DYE_GREEN.get(), "GREEN", 27, ChatColor.DARK_GREEN, Material.GREEN_DYE),
			BRICK				= new BasicDye(Lang.DYE_BRICK.get(), "BRICK", 28, ChatColor.RED, Material.BRICK), 
	        BLACK 				= new BasicDye(Lang.DYE_BLACK.get(), "BLACK", 29, ChatColor.DARK_GRAY, Material.INK_SAC),
			GOLD 				= new BasicDye(Lang.DYE_GOLD.get(), "GOLD", 30, ChatColor.GOLD, Material.GOLD_NUGGET),
			AQUA 				= new BasicDye(Lang.DYE_AQUA.get(), "AQUA", 31, ChatColor.AQUA, Material.PRISMARINE_CRYSTALS),
			LAPIS 				= new BasicDye(Lang.DYE_LAPIS.get(), "LAPIS", 32, ChatColor.BLUE, Material.LAPIS_ORE), 
			EMERALD 			= new BasicDye(Lang.DYE_EMERALD.get(), "EMERALD", 33, ChatColor.GREEN, Material.EMERALD), 
	        LIGHT_WOOD 			= new BasicDye(Lang.DYE_LIGHT_WOOD.get(), "LIGHT_WOOD", 34, ChatColor.RED, Material.BIRCH_WOOD),														
	        MAROON 				= new BasicDye(Lang.DYE_MAROON.get(), "MAROON", 35, ChatColor.DARK_RED, Material.NETHER_WART),
			WHITE_TERRACOTTA 	= new BasicDye(Lang.DYE_WHITE_TERRACOTTA.get(), "WHITE_TERRACOTTA", 36, ChatColor.DARK_GRAY, Material.EGG), 
			ORANGE_TERRACOTTA 	= new BasicDye(Lang.DYE_ORANGE_TERRACOTTA.get(), "ORANGE_TERRACOTTA", 37, ChatColor.DARK_GRAY, Material.MAGMA_CREAM), 
			MAGENTA_TERRACOTTA 	= new BasicDye(Lang.DYE_MAGENTA_TERRACOTTA.get(), "MAGENTA_TERRACOTTA", 38, ChatColor.DARK_GRAY, Material.BEETROOT), 
			LIGHT_BLUE_TERRACOTTA = new BasicDye(Lang.DYE_LIGHT_BLUE_TERRACOTTA.get(), "LIGHT_BLUE_TERRACOTTA", 39, ChatColor.DARK_GRAY, Material.MYCELIUM),																															
			YELLOW_TERRACOTTA 	= new BasicDye(Lang.DYE_YELLOW_TERRACOTTA.get(), "YELLOW_TERRACOTTA", 40, ChatColor.DARK_GRAY, Material.GLOWSTONE_DUST), 
			LIME_TERRACOTTA 	= new BasicDye(Lang.DYE_LIME_TERRACOTTA.get(), "LIME_TERRACOTTA", 41, ChatColor.GREEN, Material.SLIME_BALL), 
			PINK_TERRACOTTA 	= new BasicDye(Lang.DYE_PINK_TERRACOTTA.get(), "PINK_TERRACOTTA", 42, ChatColor.RED, Material.SPIDER_EYE), 
			GRAY_TERRACOTTA 	= new BasicDye(Lang.DYE_GRAY_TERRACOTTA.get(), "GRAY_TERRACOTTA", 43, ChatColor.DARK_GRAY, Material.SOUL_SAND), 
			LIGHT_GRAY_TERRACOTTA = new BasicDye(Lang.DYE_LIGHT_GRAY_TERRACOTTA.get(), "LIGHT_GRAY_TERaRACOTTA", 44, ChatColor.DARK_GRAY, Material.BROWN_MUSHROOM), 
			CYAN_TERRACOTTA 	= new BasicDye(Lang.DYE_CYAN_TERRACOTTA.get(), "CYAN_TERRACOTTA", 45, ChatColor.AQUA, Material.IRON_NUGGET), 
			PURPLE_TERRACOTTA 	= new BasicDye(Lang.DYE_PURPLE_TERRACOTTA.get(), "PURPLE_TERRACOTTA", 46, ChatColor.LIGHT_PURPLE, Material.CHORUS_FRUIT), 
			BLUE_TERRACOTTA 	= new BasicDye(Lang.DYE_BLUE_TERRACOTTA.get(), "BLUE_TERRACOTTA", 47, ChatColor.LIGHT_PURPLE, Material.PURPUR_BLOCK), 
	        BROWN_TERRACOTTA 	= new BasicDye(Lang.DYE_BROWN_TERRACOTTA.get(), "BROWN_TERRACOTTA", 48, ChatColor.DARK_GRAY, Material.PODZOL),									
			GREEN_TERRACOTTA 	= new BasicDye(Lang.DYE_GREEN_TERRACOTTA.get(), "GREEN_TERRACOTTA", 49, ChatColor.GREEN, Material.POISONOUS_POTATO), 
			RED_TERRACOTTA 		= new BasicDye(Lang.DYE_RED_TERRACOTTA.get(), "RED_TERRACOTTA", 50, ChatColor.RED, Material.APPLE), 
			BLACK_TERRACOTTA 	= new BasicDye(Lang.DYE_BLACK_TERRACOTTA.get(), "BLACK_TERRACOTTA", 51, ChatColor.DARK_GRAY, Material.CHARCOAL),
			//1.16 dyes	
			CRIMSON_NYLIUM 		= new BasicDye(Lang.DYE_CRIMSON_NYLIUM.get(), "CRIMSON_NYLIUM", 52, ChatColor.DARK_GRAY, Material.CRIMSON_NYLIUM),
			CRIMSON_STEM 		= new BasicDye(Lang.DYE_CRIMSON_STEM.get(), "CRIMSON_STEM", 53, ChatColor.DARK_GRAY, Material.CRIMSON_STEM),
			CRIMSON_HYPHAE 		= new BasicDye(Lang.DYE_CRIMSON_HYPHAE.get(), "CRIMSON_HYPHAE", 54, ChatColor.DARK_GRAY, Material.CRIMSON_HYPHAE),
			WARPNED_NYLIUM 		= new BasicDye(Lang.DYE_WARPNED_NYLIUM.get(), "WARPNED_NYLIUM", 55, ChatColor.DARK_GRAY, Material.WARPED_NYLIUM),
			WARPED_STEM 		= new BasicDye(Lang.DYE_WARPED_STEM.get(), "WARPED_STEM", 56, ChatColor.DARK_GRAY, Material.WARPED_STEM),
			WARPED_HYPHAE 		= new BasicDye(Lang.DYE_WARPED_HYPHAE.get(), "WARPED_HYPHAE", 57, ChatColor.DARK_GRAY, Material.WARPED_HYPHAE),
			WARPED_WART_BLOCK 	= new BasicDye(Lang.DYE_WARPED_WART_BLOCK.get(), "WARPED_WART_BLOCK", 58, ChatColor.DARK_GRAY, Material.WARPED_WART_BLOCK),
			//1.18 dyes
			DEEPSLATE 			= new BasicDye(Lang.DYE_DEEPSLATE.get(), "WARPED_WART_BLOCK", 59, ChatColor.DARK_GRAY, Material.COBBLED_DEEPSLATE),
			RAW_IRON 			= new BasicDye(Lang.DYE_RAW_IRON.get(), "WARPED_WART_BLOCK", 60, ChatColor.DARK_GRAY, Material.RAW_IRON),
			GLOW_LICHEN 		= new BasicDye(Lang.DYE_GLOW_LICHEN.get(), "WARPED_WART_BLOCK", 61, ChatColor.DARK_GRAY, Material.GLOW_LICHEN),
			// Shading Dyes
			COAL 	= new ShadingDye(Lang.DYE_COAL.get(), "COAL", true, ChatColor.DARK_GRAY, Material.COAL),
			FEATHER = new ShadingDye(Lang.DYE_FEATHER.get(), "FEATHER", false, ChatColor.WHITE, Material.FEATHER);

	private final ArtDye[] dyes = new ArtDye[] { BLACK, RED, GREEN, BROWN, BLUE, PURPLE, CYAN, SILVER, GRAY, PINK, LIME,
			YELLOW, LIGHT_BLUE, MAGENTA, ORANGE, WHITE, CREAM, COFFEE, GRAPHITE, GUNPOWDER, MAROON, AQUA, GRASS, GOLD,
			VOID, LIGHT_GRAY, ICE, LEAVES, SNOW, STONE, WATER, DARK_WOOD, BRICK, LAPIS, EMERALD, LIGHT_WOOD,
			WHITE_TERRACOTTA, ORANGE_TERRACOTTA, MAGENTA_TERRACOTTA, LIGHT_BLUE_TERRACOTTA, YELLOW_TERRACOTTA,
			LIME_TERRACOTTA, PINK_TERRACOTTA, GRAY_TERRACOTTA, LIGHT_GRAY_TERRACOTTA, CYAN_TERRACOTTA,
			PURPLE_TERRACOTTA, BLUE_TERRACOTTA, BROWN_TERRACOTTA, GREEN_TERRACOTTA, RED_TERRACOTTA, BLACK_TERRACOTTA,
			CRIMSON_NYLIUM, CRIMSON_STEM, CRIMSON_HYPHAE, WARPNED_NYLIUM, WARPED_STEM, WARPED_HYPHAE, WARPED_WART_BLOCK,
			DEEPSLATE, RAW_IRON, GLOW_LICHEN};

	private final ArtDye[] tools = new ArtDye[] { COAL, FEATHER };

	@Override
	public ArtDye getDye(ItemStack item) {
		for (ArtDye[] dyeList : new ArtDye[][] { dyes, tools }) {
			for (ArtDye dye : dyeList) {
				if (item.getType() == dye.getMaterial()) {
					return dye;
				}
			}
		}
		return null;
	}

	@Override
	public ArtDye[] getDyes(DyeType dyeType) {
		if (dyeType == DyeType.DYE)
			return Arrays.copyOf(dyes,dyes.length);
		else if (dyeType == DyeType.TOOL)
			return Arrays.copyOf(tools, tools.length);
		else if (dyeType == DyeType.ALL)
			return concatenate(dyes, tools);
		else
			return null;
	}

	@Override
	public ArtDye getDye(byte colour) {
		for(ArtDye dye : dyes) {
			byte base = ((BasicDye)dye).getColour();
			if(colour>=(base-1) && colour<=(base+2)) {
				return dye;
			}
		}
		return getDefaultColour();
	}

	public ArtDye[] concatenate(ArtDye[] a, ArtDye[] b) {
		int aLength = a.length;
		int bLength = b.length;
		ArtDye[] c = new ArtDye[aLength + bLength];
		System.arraycopy(a, 0, c, 0, aLength);
		System.arraycopy(b, 0, c, aLength, bLength);
		return c;
	}

	@Override
	public ArtDye getDefaultColour() {
		return WHITE;
	}

}
