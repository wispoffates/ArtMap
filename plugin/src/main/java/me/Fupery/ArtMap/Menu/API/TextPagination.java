package me.Fupery.ArtMap.Menu.API;

import java.util.UUID;

import org.bukkit.entity.Player;

import me.Fupery.ArtMap.api.Config.Lang;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;

public class TextPagination {
    UUID id;
    static final int MAX_LINES= 19;
    static final int MAX_COLUMNS = 60;
    String headerText;
    int lastpage;
    int currentPage;

    TextComponent[] text;

    public TextPagination(TextComponent[] text, String headerText, UUID playerId) {
        this.id = playerId;
        this.text = text;
        this.currentPage = 1;
        this.headerText = headerText;
        this.lastpage = (text.length/MAX_LINES)+1; //0 pages doesnt make much sense
    }
    

    public void sendPlayerNextPage(Player player) {
        if(currentPage<lastpage) {
            currentPage++;
            this.sendPlayerPage(player, currentPage);
        }
    }

    public void sendPlayerPrevPage(Player player) {
        if(currentPage>1) {
            currentPage--;
            this.sendPlayerPage(player, currentPage);
        }
    }

    public void sendPlayerPage(Player player, int page) {
        this.currentPage = page;
        int startIndex = MAX_LINES * (page-1);
        player.spigot().sendMessage(buildHeader());
        for(int i = startIndex; i<text.length && (i-startIndex)<MAX_LINES; i++) {
            player.spigot().sendMessage(text[i]);
        }
    }

    public UUID getId() {
        return this.id;
    }

    public TextComponent buildHeader() {
        TextComponent header = new TextComponent();
        //back arrows 
        if(currentPage>1) {
            TextComponent backArrow = new TextComponent(" << ");
            backArrow.setColor(ChatColor.RED);
            backArrow.setBold(true);
            backArrow.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/artmap page " + id.toString() + " " + (currentPage-1)));
            header.addExtra(backArrow);
        } else {
            header.addExtra(ChatColor.GRAY + " **");
        }
        // current page text
        String middle = ChatColor.GOLD + headerText + " :: " + ChatColor.YELLOW + Lang.SEARCH_PAGE.get() + " " 
            + ChatColor.RED +  currentPage + Lang.SEARCH_PAGE_SEPERATOR.get() + lastpage;
            
        int edgeSize = ((MAX_COLUMNS - middle.length())/2)-4;
        StringBuilder sb = new StringBuilder(edgeSize);
        for(int i=0; i<edgeSize;i++) {
            sb.append(ChatColor.GRAY + "*");
        }
        String edge = sb.toString();
        header.addExtra(edge + middle + edge);
        //forward arrow
        if(currentPage<lastpage) {
            TextComponent forwardArrow = new TextComponent(" >> ");
            forwardArrow.setColor(ChatColor.GREEN);
            forwardArrow.setBold(true);
            forwardArrow.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/artmap page " + id.toString() + " " + (currentPage+1)));
            header.addExtra(forwardArrow);
        } else {
            header.addExtra(ChatColor.GRAY + "**");
        }
        return header;
    }
}
