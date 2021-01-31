package me.Fupery.ArtMap.Command;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.api.Config.Lang;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Menu.API.TextPagination;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Search extends AsyncCommand {

    Search() {
        super(null, "/art search [--player <playername> | --mine] <searchterm>", false);
    }

    @Override
    public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
        // args[0] is search
        if (args.length < 2) {
            msg.message = Lang.SEARCH_USAGE.get();
            return;
        }

        String searchTerm = "";
        String playerName = null;
        UUID playerId = null;
        boolean mine = false;
        Player pSender = (Player) sender;

        if("--mine".equals(args[1].trim())) {
            mine = true;
            if(args.length>2) {
                searchTerm = args[2].trim();
            }
            playerId = pSender.getUniqueId();
        } else if("--player".equals(args[1].trim())) {
            playerName = args[2].trim();
            Optional<UUID> optPlayerId = ArtMap.instance().getHeadsCache().getPlayerUUID(playerName);
            if(!optPlayerId.isPresent()) {
                msg.message = "Player does not exist. :: " + playerName;
                return;
            } else {
                playerId = optPlayerId.get();
            }
            if(args.length>3) {
                searchTerm = args[3].trim();
            }
        } else {
            searchTerm = args[1];
        }

        
        MapArt[] art = new MapArt[0];
        try {
            art = ArtMap.instance().getArtDatabase().searchArtworks(searchTerm,playerId);
        } catch (SQLException e) {
            Lang.GENERIC_ERROR.send(sender);
            ArtMap.instance().getLogger().log(Level.SEVERE,"SQL Exception on search.",e);
        }

        List<TextComponent> text = new ArrayList<>();

        if(!mine && playerName == null) {
            String[] artists = ArtMap.instance().getArtDatabase().searchArtists(searchTerm);
            text.add(new TextComponent("------------- " + Lang.SEARCH_ARTISTS.get() + ": " + artists.length + " ---------------"));
            //clickable links for each artist
            for(String artist : artists) {
                text.add(this.clickableArtist(artist));
            }
        }
        text.add(new TextComponent("------------- " + Lang.SEARCH_ARTWORKS.get() +": " + art.length + " ---------------"));
        //clickable links for each artwork
        for(MapArt artwork : art) {
            text.add(this.clickableArtwork(pSender,artwork));
        }

        TextPagination pages = new TextPagination(text.toArray(new TextComponent[text.size()]), Lang.SEARCH_TITLE.get(), pSender.getUniqueId());
        Page.startPagination(pSender, pages);
    }

    private TextComponent clickableArtist(String artist) {
        TextComponent text = new TextComponent("[ "+ artist + " ]");
        text.setBold(true);
        text.setColor(ChatColor.GRAY);
        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/artmap search --player " + artist));
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{ new TextComponent(Lang.SEARCH_ARTIST_HOVER.get()) }));
        return text;
    }

    private TextComponent clickableArtwork(Player user, MapArt art) {
        TextComponent text = new TextComponent(art.getTitle());
        text.setColor(ChatColor.AQUA);
        text.addExtra(ChatColor.WHITE + Lang.SEARCH_BY.get() + ChatColor.GRAY + art.getArtistName());
        int textLen = art.getTitle().length() + art.getArtistName().length();
        StringBuilder sb = new StringBuilder();
        for(int i=textLen; i<(50-textLen); i++) {
            sb.append(".");
        }
        text.addExtra(ChatColor.DARK_GRAY+sb.toString());
        TextComponent previewButton = new TextComponent(" [" + Lang.SEARCH_PREVIEW_BUTTON.get() + "] ");
        previewButton.setColor(ChatColor.YELLOW);
        previewButton.setBold(true);
        previewButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Arrays.asList(new TextComponent(Lang.SEARCH_PREVIEW_HOVER.get())).toArray(new BaseComponent[0])));
        previewButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/art preview \"" + art.getTitle() + "\""));
        text.addExtra(previewButton);
        if(user.hasPermission("artmap.admin")) {
            //"/art give <player> <easel|canvas|paintbrush|artwork:<title>> [amount]"
            TextComponent giveButton = new TextComponent(" [" + Lang.SEARCH_GIVE_BUTTON.get() + "] ");
            giveButton.setColor(ChatColor.DARK_GREEN);
            giveButton.setBold(true);
            giveButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Arrays.asList(new TextComponent(Lang.SEARCH_GIVE_HOVER.get())).toArray(new BaseComponent[0])));
            giveButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/art give " + user.getName() + " \"artwork:" + art.getTitle() +"\""));
            text.addExtra(giveButton);
        }
        return text;
    }

}
