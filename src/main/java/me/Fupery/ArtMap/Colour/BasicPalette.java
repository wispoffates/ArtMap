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

	VOID = new BasicDye(DYE_VOID.get(), 0, ChatColor.DARK_GREEN, Material.EYE_OF_ENDER),
			GRASS = new BasicDye(DYE_GRASS.get(), 1, ChatColor.DARK_GREEN, Material.SEEDS),
			CREAM = new BasicDye(DYE_CREAM.get(), 2, ChatColor.GOLD, Material.PUMPKIN_SEEDS),
			LIGHT_GRAY = new BasicDye(Lang.DYE_LIGHT_GRAY.get(), 3, ChatColor.GRAY, Material.WEB), // new
			RED = new BasicDye(DYE_RED.get(), 4, ChatColor.RED, Material.INK_SACK, 1),
			ICE = new BasicDye(Lang.DYE_ICE.get(), 5, ChatColor.GRAY, Material.ICE), // new
			SILVER = new BasicDye(DYE_SILVER.get(), 6, ChatColor.GRAY, Material.INK_SACK, 7),
			LEAVES = new BasicDye(Lang.DYE_LEAVES.get(), 7, ChatColor.GREEN, Material.LEAVES), // new
			SNOW = new BasicDye(Lang.DYE_SNOW.get(), 8, ChatColor.BLUE, Material.SNOW), // new
			GRAY = new BasicDye(DYE_GRAY.get(), 9, ChatColor.DARK_GRAY, Material.INK_SACK, 8),
			COFFEE = new BasicDye(DYE_COFFEE.get(), 10, ChatColor.DARK_RED, Material.MELON_SEEDS),
			STONE = new BasicDye(Lang.DYE_STONE.get(), 11, ChatColor.DARK_GRAY, Material.GHAST_TEAR), // new
			WATER = new BasicDye(Lang.DYE_WATER.get(), 12, ChatColor.DARK_BLUE, Material.LAPIS_BLOCK), // new
			DARK_WOOD = new BasicDye(Lang.DYE_DARK_WOOD.get(), 13, ChatColor.GREEN, Material.WOOD, 1), // new
			WHITE = new BasicDye(DYE_WHITE.get(), 14, ChatColor.WHITE, Material.INK_SACK, 15),
			ORANGE = new BasicDye(DYE_ORANGE.get(), 15, ChatColor.GOLD, Material.INK_SACK, 14),
			MAGENTA = new BasicDye(DYE_MAGENTA.get(), 16, ChatColor.LIGHT_PURPLE, Material.INK_SACK, 13),
			LIGHT_BLUE = new BasicDye(DYE_LIGHT_BLUE.get(), 17, ChatColor.BLUE, Material.INK_SACK, 12),
			YELLOW = new BasicDye(DYE_YELLOW.get(), 18, ChatColor.YELLOW, Material.INK_SACK, 11),
			LIME = new BasicDye(DYE_LIME.get(), 19, ChatColor.GREEN, Material.INK_SACK, 10),
			PINK = new BasicDye(DYE_PINK.get(), 20, ChatColor.LIGHT_PURPLE, Material.INK_SACK, 9),
			GRAPHITE = new BasicDye(DYE_GRAPHITE.get(), 21, ChatColor.DARK_GRAY, Material.FLINT),
			GUNPOWDER = new BasicDye(DYE_GUNPOWDER.get(), 22, ChatColor.GRAY, Material.SULPHUR),
			CYAN = new BasicDye(DYE_CYAN.get(), 23, ChatColor.DARK_AQUA, Material.INK_SACK, 6),
			PURPLE = new BasicDye(DYE_PURPLE.get(), 24, ChatColor.DARK_PURPLE, Material.INK_SACK, 5),
			BLUE = new BasicDye(DYE_BLUE.get(), 25, ChatColor.DARK_BLUE, Material.INK_SACK, 4),
			BROWN = new BasicDye(DYE_BROWN.get(), 26, ChatColor.DARK_RED, Material.INK_SACK, 3),
			GREEN = new BasicDye(DYE_GREEN.get(), 27, ChatColor.DARK_GREEN, Material.INK_SACK, 2),
			BRICK = new BasicDye(Lang.DYE_BRICK.get(), 28, ChatColor.RED, Material.BRICK), // new
			BLACK = new BasicDye(DYE_BLACK.get(), 29, ChatColor.DARK_GRAY, Material.INK_SACK, 0),
			GOLD = new BasicDye(DYE_GOLD.get(), 30, ChatColor.GOLD, Material.GOLD_NUGGET),
			AQUA = new BasicDye(DYE_AQUA.get(), 31, ChatColor.AQUA, Material.PRISMARINE_CRYSTALS),
			LAPIS = new BasicDye(Lang.DYE_LAPIS.get(), 32, ChatColor.BLUE, Material.LAPIS_ORE), // new
			EMERALD = new BasicDye(Lang.DYE_EMERALD.get(), 33, ChatColor.GREEN, Material.EMERALD), // new
			LIGHT_WOOD = new BasicDye(Lang.DYE_LIGHT_WOOD.get(), 34, ChatColor.RED, Material.WOOD, 2), // new
			MAROON = new BasicDye(DYE_MAROON.get(), 35, ChatColor.DARK_RED, Material.NETHER_STALK),
			WHITE_TERRACOTTA = new BasicDye(Lang.DYE_WHITE_TERRACOTTA.get(), 36, ChatColor.DARK_GRAY, Material.EGG), // new
			ORANGE_TERRACOTTA = new BasicDye(Lang.DYE_ORANGE_TERRACOTTA.get(), 37, ChatColor.DARK_GRAY,
					Material.MAGMA_CREAM), // new
			MAGENTA_TERRACOTTA = new BasicDye(Lang.DYE_MAGENTA_TERRACOTTA.get(), 38, ChatColor.DARK_GRAY,
					Material.BEETROOT), // new
			LIGHT_BLUE_TERRACOTTA = new BasicDye(Lang.DYE_LIGHT_BLUE_TERRACOTTA.get(), 39, ChatColor.DARK_GRAY,
					Material.MYCEL), // new
			YELLOW_TERRACOTTA = new BasicDye(Lang.DYE_YELLOW_TERRACOTTA.get(), 40, ChatColor.DARK_GRAY,
					Material.GLOWSTONE_DUST), // new
			LIME_TERRACOTTA = new BasicDye(Lang.DYE_LIME_TERRACOTTA.get(), 41, ChatColor.GREEN, Material.SLIME_BALL), // new
			PINK_TERRACOTTA = new BasicDye(Lang.DYE_PINK_TERRACOTTA.get(), 42, ChatColor.RED, Material.SPIDER_EYE), // new
			GRAY_TERRACOTTA = new BasicDye(Lang.DYE_GRAY_TERRACOTTA.get(), 43, ChatColor.DARK_GRAY, Material.SOUL_SAND), // new
			LIGHT_GRAY_TERRACOTTA = new BasicDye(Lang.DYE_LIGHT_GRAY_TERRACOTTA.get(), 44, ChatColor.DARK_GRAY,
					Material.BROWN_MUSHROOM), // new
			CYAN_TERRACOTTA = new BasicDye(Lang.DYE_CYAN_TERRACOTTA.get(), 45, ChatColor.AQUA, Material.IRON_NUGGET), // new
			PURPLE_TERRACOTTA = new BasicDye(Lang.DYE_PURPLE_TERRACOTTA.get(), 46, ChatColor.LIGHT_PURPLE,
					Material.CHORUS_FRUIT), // new
			BLUE_TERRACOTTA = new BasicDye(Lang.DYE_BLUE_TERRACOTTA.get(), 47, ChatColor.DARK_BLUE,
					Material.PURPUR_BLOCK), // new
			BROWN_TERRACOTTA = new BasicDye(Lang.DYE_BROWN_TERRACOTTA.get(), 48, ChatColor.DARK_GRAY, Material.DIRT, 2), // new
			GREEN_TERRACOTTA = new BasicDye(Lang.DYE_GREEN_TERRACOTTA.get(), 49, ChatColor.GREEN,
					Material.POISONOUS_POTATO), // new
			RED_TERRACOTTA = new BasicDye(Lang.DYE_RED_TERRACOTTA.get(), 50, ChatColor.RED, Material.APPLE), // new
			BLACK_TERRACOTTA = new BasicDye(Lang.DYE_BLACK_TERRACOTTA.get(), 51, ChatColor.DARK_GRAY, Material.COAL, 1), // new

			// Shading Dyes
			COAL = new ShadingDye(DYE_COAL.get(), true, ChatColor.DARK_GRAY, Material.COAL),
			FEATHER = new ShadingDye(DYE_FEATHER.get(), false, ChatColor.WHITE, Material.FEATHER);

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
					if (dye.getDurability() != -1) {
						if (item.getDurability() != dye.getDurability()) {
							continue;
						}
					}
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
