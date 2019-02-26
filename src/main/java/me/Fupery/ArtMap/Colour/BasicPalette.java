package me.Fupery.ArtMap.Colour;

import static me.Fupery.ArtMap.Config.Lang.DYE_AQUA;
import static me.Fupery.ArtMap.Config.Lang.DYE_BLACK;
import static me.Fupery.ArtMap.Config.Lang.DYE_BLUE;
import static me.Fupery.ArtMap.Config.Lang.DYE_BROWN;
import static me.Fupery.ArtMap.Config.Lang.DYE_COAL;
import static me.Fupery.ArtMap.Config.Lang.DYE_COFFEE;
import static me.Fupery.ArtMap.Config.Lang.DYE_CREAM;
import static me.Fupery.ArtMap.Config.Lang.DYE_CYAN;
import static me.Fupery.ArtMap.Config.Lang.DYE_FEATHER;
import static me.Fupery.ArtMap.Config.Lang.DYE_GOLD;
import static me.Fupery.ArtMap.Config.Lang.DYE_GRAPHITE;
import static me.Fupery.ArtMap.Config.Lang.DYE_GRASS;
import static me.Fupery.ArtMap.Config.Lang.DYE_GRAY;
import static me.Fupery.ArtMap.Config.Lang.DYE_GREEN;
import static me.Fupery.ArtMap.Config.Lang.DYE_GUNPOWDER;
import static me.Fupery.ArtMap.Config.Lang.DYE_LIGHT_BLUE;
import static me.Fupery.ArtMap.Config.Lang.DYE_LIME;
import static me.Fupery.ArtMap.Config.Lang.DYE_MAGENTA;
import static me.Fupery.ArtMap.Config.Lang.DYE_MAROON;
import static me.Fupery.ArtMap.Config.Lang.DYE_ORANGE;
import static me.Fupery.ArtMap.Config.Lang.DYE_PINK;
import static me.Fupery.ArtMap.Config.Lang.DYE_PURPLE;
import static me.Fupery.ArtMap.Config.Lang.DYE_RED;
import static me.Fupery.ArtMap.Config.Lang.DYE_SILVER;
import static me.Fupery.ArtMap.Config.Lang.DYE_VOID;
import static me.Fupery.ArtMap.Config.Lang.DYE_WHITE;
import static me.Fupery.ArtMap.Config.Lang.DYE_YELLOW;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.Fupery.ArtMap.Config.Lang;

public class BasicPalette implements Palette {
	public final ArtDye

			VOID  		= new BasicDye(DYE_VOID.get(), "VOID", 0, ChatColor.DARK_GREEN, Material.ENDER_EYE),
	        GRASS 		= new BasicDye(DYE_GRASS.get(), "GRASS", 1, ChatColor.DARK_GREEN, Material.GRASS),
			CREAM 		= new BasicDye(DYE_CREAM.get(), "CREAM", 2, ChatColor.GOLD, Material.PUMPKIN_SEEDS),
	        LIGHT_GRAY 	= new BasicDye(Lang.DYE_LIGHT_GRAY.get(), "LIGHT_GRAY", 3, ChatColor.GRAY, Material.COBWEB),															// new
	        RED 		= new BasicDye(DYE_RED.get(), "RED", 4, ChatColor.RED, Material.ROSE_RED),
			ICE 		= new BasicDye(Lang.DYE_ICE.get(), "ICE", 5, ChatColor.GRAY, Material.ICE), // new
	        SILVER 		= new BasicDye(DYE_SILVER.get(), "SILVER", 6, ChatColor.GRAY, Material.LIGHT_GRAY_DYE),
	        LEAVES 		= new BasicDye(Lang.DYE_LEAVES.get(), "LEAVES", 7, ChatColor.GREEN, Material.OAK_LEAVES),																// new
			SNOW 		= new BasicDye(Lang.DYE_SNOW.get(), "SNOW", 8, ChatColor.BLUE, Material.SNOW), // new
	        GRAY 		= new BasicDye(DYE_GRAY.get(), "GRAY", 9, ChatColor.DARK_GRAY, Material.GRAY_DYE),
			COFFEE 		= new BasicDye(DYE_COFFEE.get(), "COFFEE", 10, ChatColor.DARK_RED, Material.MELON_SEEDS),
			STONE 		= new BasicDye(Lang.DYE_STONE.get(), "STONE", 11, ChatColor.DARK_GRAY, Material.GHAST_TEAR), // new
			WATER 		= new BasicDye(Lang.DYE_WATER.get(), "WATER", 12, ChatColor.DARK_BLUE, Material.LAPIS_BLOCK), // new
	        DARK_WOOD	= new BasicDye(Lang.DYE_DARK_WOOD.get(), "DARK_WOOD", 13, ChatColor.GREEN, Material.OAK_WOOD),															// new
			WHITE 		= new BasicDye(DYE_WHITE.get(), "WHITE", 14, ChatColor.WHITE, Material.BONE_MEAL),
	        ORANGE 		= new BasicDye(DYE_ORANGE.get(), "ORANGE", 15, ChatColor.GOLD, Material.ORANGE_DYE),
	        MAGENTA 	= new BasicDye(DYE_MAGENTA.get(), "MAGENTA", 16, ChatColor.LIGHT_PURPLE, Material.MAGENTA_DYE),
	        LIGHT_BLUE 	= new BasicDye(DYE_LIGHT_BLUE.get(), "LIGHT_BLUE", 17, ChatColor.BLUE, Material.LIGHT_BLUE_DYE),
	        YELLOW 		= new BasicDye(DYE_YELLOW.get(), "YELLOW", 18, ChatColor.YELLOW, Material.DANDELION_YELLOW),
	        LIME 		= new BasicDye(DYE_LIME.get(), "LIME", 19, ChatColor.GREEN, Material.LIME_DYE),
	        PINK 		= new BasicDye(DYE_PINK.get(), "PINK", 20, ChatColor.LIGHT_PURPLE, Material.PINK_DYE),
			GRAPHITE 	= new BasicDye(DYE_GRAPHITE.get(), "GRAPHITE", 21, ChatColor.DARK_GRAY, Material.FLINT),
	        GUNPOWDER	= new BasicDye(DYE_GUNPOWDER.get(), "GUNPOWDER", 22, ChatColor.GRAY, Material.GUNPOWDER),
	        CYAN 		= new BasicDye(DYE_CYAN.get(), "CYAN", 23, ChatColor.DARK_AQUA, Material.CYAN_DYE),
	        PURPLE 		= new BasicDye(DYE_PURPLE.get(), "PURPLE", 24, ChatColor.DARK_PURPLE, Material.PURPLE_DYE),
	        BLUE 		= new BasicDye(DYE_BLUE.get(), "BLUE", 25, ChatColor.DARK_BLUE, Material.LAPIS_LAZULI),
	        BROWN 		= new BasicDye(DYE_BROWN.get(), "BROWN", 26, ChatColor.DARK_RED, Material.COCOA_BEANS),
	        GREEN 		= new BasicDye(DYE_GREEN.get(), "GREEN", 27, ChatColor.DARK_GREEN, Material.CACTUS_GREEN),
			BRICK		= new BasicDye(Lang.DYE_BRICK.get(), "BRICK", 28, ChatColor.RED, Material.BRICK), // new
	        BLACK 		= new BasicDye(DYE_BLACK.get(), "BLACK", 29, ChatColor.DARK_GRAY, Material.INK_SAC),
			GOLD 		= new BasicDye(DYE_GOLD.get(), "GOLD", 30, ChatColor.GOLD, Material.GOLD_NUGGET),
			AQUA 		= new BasicDye(DYE_AQUA.get(), "AQUA", 31, ChatColor.AQUA, Material.PRISMARINE_CRYSTALS),
			LAPIS 		= new BasicDye(Lang.DYE_LAPIS.get(), "LAPIS", 32, ChatColor.BLUE, Material.LAPIS_ORE), // new
			EMERALD 	= new BasicDye(Lang.DYE_EMERALD.get(), "EMERALD", 33, ChatColor.GREEN, Material.EMERALD), // new
	        LIGHT_WOOD 	= new BasicDye(Lang.DYE_LIGHT_WOOD.get(), "LIGHT_WOOD", 34, ChatColor.RED, Material.BIRCH_WOOD),														// new
	        MAROON 		= new BasicDye(DYE_MAROON.get(), "MAROON", 35, ChatColor.DARK_RED, Material.NETHER_WART),
			WHITE_TERRACOTTA 	= new BasicDye(Lang.DYE_WHITE_TERRACOTTA.get(), "WHITE_TERRACOTTA", 36, ChatColor.DARK_GRAY, Material.EGG), // new
			ORANGE_TERRACOTTA 	= new BasicDye(Lang.DYE_ORANGE_TERRACOTTA.get(), "ORANGE_TERRACOTTA", 37, ChatColor.DARK_GRAY, Material.MAGMA_CREAM), // new
			MAGENTA_TERRACOTTA 	= new BasicDye(Lang.DYE_MAGENTA_TERRACOTTA.get(), "MAGENTA_TERRACOTTA", 38, ChatColor.DARK_GRAY, Material.BEETROOT), // new
			LIGHT_BLUE_TERRACOTTA = new BasicDye(Lang.DYE_LIGHT_BLUE_TERRACOTTA.get(), "LIGHT_BLUE_TERRACOTTA", 39, ChatColor.DARK_GRAY, Material.MYCELIUM),																															// new
			YELLOW_TERRACOTTA 	= new BasicDye(Lang.DYE_YELLOW_TERRACOTTA.get(), "YELLOW_TERRACOTTA", 40, ChatColor.DARK_GRAY, Material.GLOWSTONE_DUST), // new
			LIME_TERRACOTTA 	= new BasicDye(Lang.DYE_LIME_TERRACOTTA.get(), "LIME_TERRACOTTA", 41, ChatColor.GREEN, Material.SLIME_BALL), // new
			PINK_TERRACOTTA 	= new BasicDye(Lang.DYE_PINK_TERRACOTTA.get(), "PINK_TERRACOTTA", 42, ChatColor.RED, Material.SPIDER_EYE), // new
			GRAY_TERRACOTTA 	= new BasicDye(Lang.DYE_GRAY_TERRACOTTA.get(), "GRAY_TERRACOTTA", 43, ChatColor.DARK_GRAY, Material.SOUL_SAND), // new
			LIGHT_GRAY_TERRACOTTA = new BasicDye(Lang.DYE_LIGHT_GRAY_TERRACOTTA.get(), "LIGHT_GRAY_TERRACOTTA", 44, ChatColor.DARK_GRAY, Material.BROWN_MUSHROOM), // new
			CYAN_TERRACOTTA 	= new BasicDye(Lang.DYE_CYAN_TERRACOTTA.get(), "CYAN_TERRACOTTA", 45, ChatColor.AQUA, Material.IRON_NUGGET), // new
			PURPLE_TERRACOTTA 	= new BasicDye(Lang.DYE_PURPLE_TERRACOTTA.get(), "PURPLE_TERRACOTTA", 46, ChatColor.LIGHT_PURPLE, Material.CHORUS_FRUIT), // new
			BLUE_TERRACOTTA 	= new BasicDye(Lang.DYE_BLUE_TERRACOTTA.get(), "BLUE_TERRACOTTA", 47, ChatColor.DARK_BLUE, Material.PURPUR_BLOCK), // new
	        BROWN_TERRACOTTA 	= new BasicDye(Lang.DYE_BROWN_TERRACOTTA.get(), "BROWN_TERRACOTTA", 48, ChatColor.DARK_GRAY, Material.COARSE_DIRT),									// new
			GREEN_TERRACOTTA 	= new BasicDye(Lang.DYE_GREEN_TERRACOTTA.get(), "GREEN_TERRACOTTA", 49, ChatColor.GREEN, Material.POISONOUS_POTATO), // new
			RED_TERRACOTTA 		= new BasicDye(Lang.DYE_RED_TERRACOTTA.get(), "RED_TERRACOTTA", 50, ChatColor.RED, Material.APPLE), // new
	        BLACK_TERRACOTTA 	= new BasicDye(Lang.DYE_BLACK_TERRACOTTA.get(), "BLACK_TERRACOTTA", 51, ChatColor.DARK_GRAY, Material.CHARCOAL),										// new

			// Shading Dyes
			COAL 	= new ShadingDye(DYE_COAL.get(), "COAL", true, ChatColor.DARK_GRAY, Material.COAL),
			FEATHER = new ShadingDye(DYE_FEATHER.get(), "FEATHER", false, ChatColor.WHITE, Material.FEATHER);

	private final ArtDye[] dyes = new ArtDye[] { BLACK, RED, GREEN, BROWN, BLUE, PURPLE, CYAN, SILVER, GRAY, PINK, LIME,
			YELLOW, LIGHT_BLUE, MAGENTA, ORANGE, WHITE, CREAM, COFFEE, GRAPHITE, GUNPOWDER, MAROON, AQUA, GRASS, GOLD,
			VOID, LIGHT_GRAY, ICE, LEAVES, SNOW, STONE, WATER, DARK_WOOD, BRICK, LAPIS, EMERALD, LIGHT_WOOD,
			WHITE_TERRACOTTA, ORANGE_TERRACOTTA, MAGENTA_TERRACOTTA, LIGHT_BLUE_TERRACOTTA, YELLOW_TERRACOTTA,
			LIME_TERRACOTTA, PINK_TERRACOTTA, GRAY_TERRACOTTA, LIGHT_GRAY_TERRACOTTA, CYAN_TERRACOTTA,
			PURPLE_TERRACOTTA, BLUE_TERRACOTTA, BROWN_TERRACOTTA, GREEN_TERRACOTTA, RED_TERRACOTTA, BLACK_TERRACOTTA };

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
			return dyes;
		else if (dyeType == DyeType.TOOL)
			return tools;
		else if (dyeType == DyeType.ALL)
			return concatenate(dyes, tools);
		else
			return null;
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
	public BasicDye getDefaultColour() {
		return ((BasicDye) WHITE);
	}

}
