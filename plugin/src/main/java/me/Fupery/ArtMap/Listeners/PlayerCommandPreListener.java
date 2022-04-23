package me.Fupery.ArtMap.Listeners;


import java.util.HashSet;
import java.util.Set;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.api.Config.Lang;
import net.md_5.bungee.api.ChatColor;
	
class PlayerCommandPreListener implements RegisteredListener {
	
	private Set<String> blacklist = new HashSet<>();
	
	public PlayerCommandPreListener(){
		String blacklistdata = ArtMap.instance().getConfiguration().BLACKLIST;
		if(blacklistdata != null){
			String[] commandarray = blacklistdata.split(",");
			for (String command: commandarray) {
				blacklist.add(command.trim());
			}
		}
	}
	
	@EventHandler
	public void onCommandPre(PlayerCommandPreprocessEvent event){
		if(ArtMap.instance().getArtistHandler().getCurrentSession(event.getPlayer()) != null &&
			ArtMap.instance().getArtistHandler().getCurrentSession(event.getPlayer()).isInArtKit()){
			String message = event.getMessage().toLowerCase();
			if(message.startsWith("/")) {
				for(String disallowed : this.blacklist) {
					if(message.startsWith(disallowed, 1)) {
						event.setCancelled(true);
						event.getPlayer().sendMessage(Lang.PREFIX + ChatColor.RED + "This command can't be used at an easel.");
					}
				}
			}
		}
	}
	
    @Override
    public void unregister() {
        PlayerCommandPreprocessEvent.getHandlerList().unregister(this);
	}
}
