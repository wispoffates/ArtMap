package me.Fupery.ArtMap.Painting;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.api.Config.Lang;
import me.Fupery.ArtMap.Easel.Canvas;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.Easel.EaselEffect;
import me.Fupery.ArtMap.Exception.ArtMapException;
import me.Fupery.ArtMap.Exception.DuplicateArtworkException;
import me.Fupery.ArtMap.Exception.PermissionException;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.IO.TitleFilter;
import me.Fupery.ArtMap.IO.Database.Map;
import me.Fupery.ArtMap.IO.Protocol.In.Packet.ArtistPacket;
import me.Fupery.ArtMap.IO.Protocol.In.Packet.ArtistPacket.PacketInteract.InteractType;
import me.Fupery.ArtMap.IO.Protocol.In.Packet.PacketType;
import me.Fupery.ArtMap.Painting.Brush.BrushAction;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import me.Fupery.ArtMap.Utils.ItemUtils;
import me.Fupery.ArtMap.api.Painting.IArtistHandler;
import net.wesjd.anvilgui.AnvilGUI;
import net.wesjd.anvilgui.AnvilGUI.Response;

public class ArtistHandler implements IArtistHandler {

	private final ConcurrentHashMap<UUID, ArtSession> artists;
	// todo replaced synchronised methods with read/write lock

	public ArtistHandler() {
		artists = new ConcurrentHashMap<>();
	}

	public boolean handlePacket(Player sender, ArtistPacket packet) {
		if (packet == null || sender == null) {
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
				return this.handlePaintBrush(sender, session);
			} else if (type == PacketType.INTERACT) {
				InteractType click = ((ArtistPacket.PacketInteract) packet).getInteraction();
				session.paint(sender.getInventory().getItemInMainHand(),
						(click == InteractType.ATTACK) ? BrushAction.LEFT_CLICK : BrushAction.RIGHT_CLICK);
				session.sendMap(sender);
				return false;
			}
		} else {
			try {
				removePlayer(sender);
			} catch (SQLException | IOException e) {
				ArtMap.instance().getLogger().log(Level.SEVERE, "Error exiting art session!", e);
			}
		}
		return true;
	}

	private boolean handlePaintBrush(Player sender, ArtSession session) {
		// check if the paintbrush has been disabled
		if (ArtMap.instance().getConfiguration().DISABLE_PAINTBRUSH) {
			Lang.PAINTBRUSH_DISABLED.send(sender);
			return false;
		}

		// handle click with paint brush in main hand causes save
		if (sender.isInsideVehicle() && ArtMap.instance().getArtistHandler().containsPlayer(sender)) {
			ArtMap.instance().getScheduler().SYNC.run(() -> {
				AnvilGUI.Builder builder = new AnvilGUI.Builder();
				builder.plugin(ArtMap.instance()).text("Title?").onComplete((player, title) -> {
					TitleFilter filter = new TitleFilter(Lang.Filter.ILLEGAL_EXPRESSIONS.get());
					if (title == null || !filter.check(title)) {
						player.sendMessage(Lang.BAD_TITLE.get());
						return Response.close();
					}
					Easel easel = session.getEasel();
					ArtMap.instance().getScheduler().SYNC.run(() -> {
						try {
							Canvas canvas = Canvas.getCanvas(easel.getItem());
							if (canvas == null) {
								ArtMap.instance().getLogger().log(Level.SEVERE, "Canvas should not be null!");
								Lang.GENERIC_ERROR.send(player);
								return;
							}
							MapArt art1 = ArtMap.instance().getArtDatabase().saveArtwork(canvas, title, player);
							ArtMap.instance().getArtistHandler().removePlayer(player);
							easel.setItem(new ItemStack(Material.AIR));
							ItemUtils.giveItem(player, art1.getMapItem());
							player.sendMessage(String.format(Lang.PREFIX + Lang.SAVE_SUCCESS.get(), title));
							easel.playEffect(EaselEffect.SAVE_ARTWORK);
						} catch (DuplicateArtworkException | PermissionException e) {
							player.sendMessage(e.getMessage());
						} catch (SQLException | IOException | NoSuchFieldException | IllegalAccessException | ArtMapException sqe) {
							player.sendMessage(String.format(Lang.PREFIX + Lang.SAVE_FAILURE.get(), title));
							ArtMap.instance().getLogger().log(Level.SEVERE, "Error saving artwork!", sqe);
						} 
					});
					return Response.close();
				});
				builder.open(sender);
			});
			return true;
		}
		return false;
	}

	public synchronized void addPlayer(final Player player, Easel easel, Map map, int yawOffset)
			throws ReflectiveOperationException, SQLException, IOException {
		ArtSession session = new ArtSession(player, easel, map, yawOffset);
		if (session.start(player)) {
			ArtMap.instance().getProtocolManager().PACKET_RECIEVER.injectPlayer(player);
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
		if(player==null) {
			return false;
		}
		return (artists.containsKey(player.getUniqueId()));
	}

	public boolean containsPlayer(UUID player) {
		if(player==null) {
			return false;
		}
		return artists.containsKey(player);
	}

	public synchronized void removePlayer(final Player player) throws SQLException, IOException {
		if (!containsPlayer(player))
			return;// just for safety :)
		ArtSession session = artists.get(player.getUniqueId());
		if (!session.isActive())
			return;
		artists.remove(player.getUniqueId());
		session.end(player);
		ArtMap.instance().getProtocolManager().PACKET_RECIEVER.uninjectPlayer(player);
	}

	public ArtSession getCurrentSession(Player player) {
		return artists.get(player.getUniqueId());
	}

	public ArtSession getCurrentSession(UUID player) {
		return artists.get(player);
	}

	private synchronized void clearPlayers() {
		for (UUID uuid : artists.keySet()) {
			try {
				removePlayer(Bukkit.getPlayer(uuid));
			} catch (SQLException | IOException e) {
				ArtMap.instance().getLogger().log(Level.SEVERE,"Error clearing players art session!",e);
			}
		}
	}

	public Set<UUID> getArtists() {
		return artists.keySet();
	}

	public void stop() {
		clearPlayers();
		ArtMap.instance().getProtocolManager().PACKET_RECIEVER.close();
	}
}