package me.Fupery.ArtMap.Painting;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Config.Lang;
import me.Fupery.ArtMap.Easel.Canvas;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.Easel.EaselEffect;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.IO.TitleFilter;
import me.Fupery.ArtMap.IO.Database.Map;
import me.Fupery.ArtMap.IO.Protocol.In.Packet.ArtistPacket;
import me.Fupery.ArtMap.IO.Protocol.In.Packet.ArtistPacket.PacketInteract.InteractType;
import me.Fupery.ArtMap.IO.Protocol.In.Packet.PacketType;
import me.Fupery.ArtMap.Painting.Brush.BrushAction;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import me.Fupery.ArtMap.Utils.ItemUtils;
import net.wesjd.anvilgui.AnvilGUI;

public class ArtistHandler {

	private final ConcurrentHashMap<UUID, ArtSession> artists;
	// todo replaced synchronised methods with read/write lock

	public ArtistHandler() {
		artists = new ConcurrentHashMap<>();
	}

	public boolean handlePacket(Player sender, ArtistPacket packet) {
		if (packet == null) {
			return true;
		}
		if (artists.containsKey(sender.getUniqueId())) {
			ArtSession session = artists.get(sender.getUniqueId());
			PacketType type = packet.getType();

			if (type == PacketType.LOOK) {
				ArtistPacket.PacketLook packetLook = (ArtistPacket.PacketLook) packet;
				session.updatePosition(packetLook.getYaw(), packetLook.getPitch());
				return true;
				// Handle Save brush
			} else if (type == PacketType.INTERACT && ArtMaterial
					.getCraftItemType(sender.getInventory().getItemInMainHand()) == ArtMaterial.PAINT_BRUSH) {
				// handle click with paint brush in main hand causes save
				if (sender.isInsideVehicle() && ArtMap.getArtistHandler().containsPlayer(sender)) {
					ArtMap.getScheduler().SYNC.run(() -> {
						new AnvilGUI(ArtMap.instance(), sender, "Title?", (player, title) -> {
							TitleFilter filter = new TitleFilter(Lang.Filter.ILLEGAL_EXPRESSIONS.get());
							if (!filter.check(title)) {
								player.sendMessage(Lang.BAD_TITLE.get());
								return null;
							}
							Easel easel = session.getEasel();
							ArtMap.getScheduler().SYNC.run(() -> {
								easel.playEffect(EaselEffect.SAVE_ARTWORK);
								Canvas canvas = Canvas.getCanvas(easel.getItem());
								MapArt art1 = ArtMap.getArtDatabase().saveArtwork(canvas, title, player);
								if (art1 != null) {
									ArtMap.getArtistHandler().removePlayer(player);
									easel.setItem(new ItemStack(Material.AIR));
									ItemUtils.giveItem(player, art1.getMapItem());
									player.sendMessage(String.format(Lang.PREFIX + Lang.SAVE_SUCCESS.get(), title));
								} else {
									player.sendMessage(String.format(Lang.PREFIX + Lang.SAVE_FAILURE.get(), title));
								}
							});
							return null;
						});
					});
				}
				return false;
			} else if (type == PacketType.INTERACT) {
				InteractType click = ((ArtistPacket.PacketInteract) packet).getInteraction();
				session.paint(sender.getInventory().getItemInMainHand(),
						(click == InteractType.ATTACK) ? BrushAction.LEFT_CLICK : BrushAction.RIGHT_CLICK);
				session.sendMap(sender);
				return false;
			}
		} else {
			removePlayer(sender);
		}
		return true;
	}

	public synchronized void addPlayer(final Player player, Easel easel, Map map, int yawOffset) {
		ArtSession session = new ArtSession(easel, map, yawOffset);
		if (session.start(player) && ArtMap.getProtocolManager().PACKET_RECIEVER.injectPlayer(player)) {
			artists.put(player.getUniqueId(), session);
			session.setActive(true);
		}
	}

	public Easel getEasel(Player player) {
		if (artists.containsKey(player.getUniqueId())) {
			return artists.get(player.getUniqueId()).getEasel();
		}
		return null;
	}

	public boolean containsPlayer(Player player) {
		return (artists.containsKey(player.getUniqueId()));
	}

	public boolean containsPlayer(UUID player) {
		return artists.containsKey(player);
	}

	public synchronized void removePlayer(final Player player) {
		if (!containsPlayer(player))
			return;// just for safety :)
		ArtSession session = artists.get(player.getUniqueId());
		if (!session.isActive())
			return;
		artists.remove(player.getUniqueId());
		session.end(player);
		ArtMap.getProtocolManager().PACKET_RECIEVER.uninjectPlayer(player);
	}

	public ArtSession getCurrentSession(Player player) {
		return artists.get(player.getUniqueId());
	}

	public ArtSession getCurrentSession(UUID player) {
		return artists.get(player);
	}

	private synchronized void clearPlayers() {
		for (UUID uuid : artists.keySet()) {
			removePlayer(Bukkit.getPlayer(uuid));
		}
	}

	public Set<UUID> getArtists() {
		return artists.keySet();
	}

	public void stop() {
		clearPlayers();
		ArtMap.getProtocolManager().PACKET_RECIEVER.close();
	}
}
