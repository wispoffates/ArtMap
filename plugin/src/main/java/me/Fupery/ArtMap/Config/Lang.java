package me.Fupery.ArtMap.Config;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.Fupery.ArtMap.ArtMap;

public enum Lang implements LangSet<String> {

    COMMAND_SAVE, COMMAND_DELETE, COMMAND_PREVIEW, COMMAND_RESTORE, COMMAND_BACKUP, COMMAND_LOOKUP, COMMAND_EXPORT, COMMAND_IMPORT, COMMAND_REPAIR, HELP, GENERIC_ERROR, SAVE_USAGE,
    SAVE_SUCCESS, SAVE_FAILURE, DELETED, PREVIEWING, RECIPE_HEADER, NEED_CANVAS, NO_CONSOLE, PLAYER_NOT_FOUND, NO_PERM, NOT_RIDING_EASEL, NOT_YOUR_EASEL, REPAIR_USAGE,
    BREAK_CANVAS, MAP_NOT_FOUND, NO_ARTWORKS, NO_CRAFT_PERM, BAD_TITLE, TITLE_USED, EMPTY_HAND_PREVIEW, SAVE_ARTWORK, SAVE_ARTWORK_2, 
    INVALID_DATA_TABLES, CANNOT_BUILD_DATABASE, MAP_ID_MISSING, RESTORED_SUCCESSFULY, MENU_RECIPE, MENU_ARTIST,
    MENU_ARTWORKS, MENU_DYES, MENU_HELP, MENU_TOOLS, BUTTON_CLICK, BUTTON_CLOSE, BUTTON_BACK, RECIPE_BUTTON, RECIPE_PLAYER_MAP_COPY, RECIPE_PLAYER_MAP_COPY_MISSING,
    ADMIN_RECIPE, RECIPE_HELP, RECIPE_EASEL_NAME, RECIPE_CANVAS_NAME, RECIPE_PAINT_BRUSH_NAME, RECIPE_ARTWORK_ARTIST, BUTTON_RENAME_NAME, BUTTON_DELETE_NAME, 
    BUTTON_ACCEPT_NAME, BUTTON_RENAME_TEXT, BUTTON_DELETE_TEXT, BUTTON_ACCEPT_TEXT, RENAMED, TITLE_QUESTION, PAINTBRUSH_GROUND, PAINTBRUSH_FORCED, PAINTBRUSH_DISABLED,
    ITEM_NAME_PAINTBUCKET, ITEM_NAME_COMPASS, ITEM_NAME_FEATHER, ITEM_NAME_COAL, ITEM_NAKE_FEATHER, ITEM_NAME_SPONGE,
    DYE_BLACK, DYE_RED, DYE_GREEN, DYE_BROWN, DYE_BLUE, DYE_PURPLE, DYE_CYAN, DYE_SILVER, DYE_GRAY, DYE_PINK, DYE_LIME,
    DYE_YELLOW, DYE_LIGHT_BLUE, DYE_MAGENTA, DYE_ORANGE, DYE_WHITE, DYE_CREAM, DYE_COFFEE, DYE_GRAPHITE, DYE_GUNPOWDER,
    DYE_MAROON, DYE_AQUA, DYE_GRASS, DYE_GOLD, DYE_VOID, DYE_COAL, DYE_FEATHER, DYE_ICE, DYE_LEAVES, DYE_SNOW, DYE_BLACK_TERRACOTTA, DYE_RED_TERRACOTTA, 
    DYE_GREEN_TERRACOTTA, DYE_BROWN_TERRACOTTA, DYE_BLUE_TERRACOTTA, DYE_PURPLE_TERRACOTTA, DYE_CYAN_TERRACOTTA, DYE_LIGHT_GRAY_TERRACOTTA, 
    DYE_GRAY_TERRACOTTA, DYE_PINK_TERRACOTTA, DYE_LIME_TERRACOTTA, DYE_YELLOW_TERRACOTTA, DYE_LIGHT_BLUE_TERRACOTTA, DYE_MAGENTA_TERRACOTTA, 
    DYE_ORANGE_TERRACOTTA, DYE_WHITE_TERRACOTTA, DYE_STONE, DYE_LIGHT_GRAY, DYE_BRICK, DYE_LAPIS, DYE_EMERALD, DYE_LIGHT_WOOD, DYE_WATER, DYE_DARK_WOOD,
    SEARCH_USAGE, SEARCH_PREVIEW_BUTTON, SEARCH_PREVIEW_HOVER, SEARCH_GIVE_BUTTON, SEARCH_GIVE_HOVER, SEARCH_ARTIST_HOVER, SEARCH_ARTISTS, SEARCH_ARTWORKS, SEARCH_TITLE, SEARCH_PAGE;

    public static final String PREFIX = ChatColor.AQUA + "[ArtMap] ";
    //private static final char EIGHT_POINTED_STAR = '\u2737';
    //private static final char SIX_PETALLED_FLORETTE = '\u273E';
    private String message = String.format("'%s' NOT FOUND", name());

    public static void load(ArtMap plugin, Configuration configuration) {
        LangLoader loader = new LangLoader(plugin, configuration);
        //Load basic messages
        for (Lang key : Lang.values()) {
            key.message = loader.loadString(key.name());
        }
        
        //Load array messages
        for (Array key : Array.values()) {
            key.messages = loader.loadArray(key.name());
        }

        //Load array messages
        for (ActionBar key : ActionBar.values()) {
            key.messages = loader.loadArray(key.name());
        }

        loader.save();
        Filter.ILLEGAL_EXPRESSIONS.expressions = loader.loadRegex("ILLEGAL_EXPRESSIONS");
    }

    @Override
    public void send(CommandSender sender) {
        if (message != null) sender.sendMessage(message);
    }

    @Override
    public String get() {
        return message;
    }

    public enum Array implements LangSet<String[]> {
        HELP_GETTING_STARTED, HELP_RECIPES, HELP_TOOLS, HELP_DYES, HELP_LIST, HELP_CLOSE, INFO_DYES, INFO_RECIPES,
        INFO_TOOLS, TOOL_DYE, TOOL_PAINTBUCKET, TOOL_COAL, TOOL_FEATHER, TOOL_COMPASS, RECIPE_EASEL, RECIPE_CANVAS,
		TOOL_BUCKET, TOOL_SPONGE, RECIPE_PAINT_BRUSH, CONSOLE_HELP;

        private String[] messages = null;

        @Override
        public void send(CommandSender sender) {
            if (messages != null) sender.sendMessage(messages);
        }

        @Override
        public String[] get() {
            return Arrays.copyOf(messages, messages.length);
        }
    }

    public enum ActionBar implements LangSet<String[]> {
        EASEL_HELP, NEED_CANVAS, PAINTING, SAVE_USAGE, ELSE_USING,
        NO_PERM_ACTION, NO_EDIT_PERM, INVALID_POS;

        private String[] messages = null;

        @Override
        public void send(CommandSender sender) {
            if (messages != null && sender instanceof Player) {
                Player p = (Player) sender;
                if(messages.length>1) {
                    p.sendTitle(messages[0], messages[1], 20, 40 , 20);
                } else {
                    p.sendTitle("", messages[0], 20, 40 , 20);
                }
            }
        }

        @Override
        public String[] get() {
            return Arrays.copyOf(messages, messages.length);
        }
    }

    public enum Filter implements LangSet<String[]> {
        ILLEGAL_EXPRESSIONS;

        private String[] expressions = null;

        @Override
        public void send(CommandSender sender) {
            //redundant
        }

        @Override
        public String[] get() {
            return Arrays.copyOf(expressions, expressions.length);
        }
    }
}