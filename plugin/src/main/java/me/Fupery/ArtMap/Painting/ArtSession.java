package me.Fupery.ArtMap.Painting;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.api.Colour.ArtDye;
import me.Fupery.ArtMap.api.Colour.DyeType;
import me.Fupery.ArtMap.api.Colour.Palette;
import me.Fupery.ArtMap.api.Config.Lang;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.Event.PlayerMountEaselEvent;
import me.Fupery.ArtMap.IO.Database.Map;
import me.Fupery.ArtMap.Painting.Brushes.Dropper;
import me.Fupery.ArtMap.Painting.Brushes.Dye;
import me.Fupery.ArtMap.Painting.Brushes.Fill;
import me.Fupery.ArtMap.Painting.Brushes.Flip;
import me.Fupery.ArtMap.Recipe.ArtItem;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import me.Fupery.ArtMap.Utils.ItemUtils;
import me.Fupery.ArtMap.api.Painting.IArtSession;

public class ArtSession implements IArtSession {
    private final CanvasRenderer canvas;
    private final Brush DYE;
    private final Brush FILL;
    private final Brush FLIP;
    private final Brush DROPPER;
    private final Easel easel;
    private final Map map;
    private Brush currentBrush;
    private long lastStroke;
    private ItemStack[] inventory;
    private static final HashMap<UUID, ItemStack[]> artkitHotbars = new HashMap<>();

    private boolean active = false;
    private boolean dirty = true;
    private int artkitPage = 0;

    ArtSession(Player player, Easel easel, Map map, int yawOffset) {
        this.easel = easel;
        canvas = new CanvasRenderer(map, yawOffset);
        currentBrush = null;
        lastStroke = System.currentTimeMillis();
        DYE = new Dye(canvas, player);
        DROPPER = new Dropper(canvas, player);
        FILL = new Fill(canvas, player, (Dropper) DROPPER);
        FLIP = new Flip(canvas, player);
        this.map = map;
    }

    public boolean start(Player player) throws SQLException, IOException {
        PlayerMountEaselEvent event = new PlayerMountEaselEvent(player, easel);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        boolean seated = easel.seatUser(player);
        if (!seated) {
            return false;
        }

        // Run tasks
        try {
            ArtMap.instance().getArtDatabase().restoreMap(map, true, false);
            ArtMap.instance().getScheduler().SYNC.runLater(() -> {
                if (player.getVehicle() != null)
                    Lang.ActionBar.PAINTING.send(player);
            }, 30);
            if (ArtMap.instance().getConfiguration().FORCE_ART_KIT && player.hasPermission("artmap.artkit")) {
                addKit(player);
            }
            map.setRenderer(canvas);
            persistMap(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            player.sendMessage("Error restoring painting! Check server logs for more details!");
            ArtMap.instance().getLogger().log(Level.SEVERE, "Error restoring painting on easel.", e);
            event.setCancelled(true);
            return false;
        }
        return true;
    }

    void paint(ItemStack brush, Brush.BrushAction action) {
        if (!dirty)
            dirty = true;
        if (currentBrush == null || !currentBrush.checkMaterial(brush)) {
            if (currentBrush != null)
                currentBrush.clean();
            currentBrush = getBrushType(brush);
        }
        if (currentBrush == null || canvas.isOffCanvas())
            return;

        long currentTime = System.currentTimeMillis();
        long strokeTime = currentTime - lastStroke;
        if (strokeTime > currentBrush.getCooldown()) {
            currentBrush.paint(action, brush, strokeTime);
        }
        lastStroke = System.currentTimeMillis();
    }

    private Brush getBrushType(ItemStack item) {
        for (Brush brush : new Brush[] { DYE, FILL, FLIP, DROPPER }) {
            if (brush.checkMaterial(item)) {
                return brush;
            }
        }
        return null;
    }

    public void updatePosition(float yaw, float pitch) {
        canvas.setYaw(yaw);
        canvas.setPitch(pitch);
    }

    private void addKit(Player player) {
        PlayerInventory pInv = player.getInventory();
        this.inventory = pInv.getContents();
        pInv.setStorageContents(this.getArtKit(0));
        // restore hotbar
        if (artkitHotbars.containsKey(player.getUniqueId())) {
            ItemStack[] hotbar = artkitHotbars.get(player.getUniqueId());
            for (int i = 0; i < 9; i++) {
                player.getInventory().setItem(i, hotbar[i]);
            }
            player.getInventory().setItemInOffHand(hotbar[9]);
        }
		else {
            player.getInventory().setItemInOffHand(null);
		}
    }

    public void nextKitPage(Player player) {
        if (this.artkitPage < this.numPages()-1) {
            this.artkitPage++;
            this.updateKitPage(player);
        }
    }

    public void prevKitPage(Player player) {
        if (this.artkitPage > 0) {
            this.artkitPage--;
            this.updateKitPage(player);
        }
    }

    /*
     * Set the contents of the inventory without replacing the hotkey bar.
     */
    private void updateKitPage(Player player) {
        ItemStack[] kit = this.getArtKit(this.artkitPage);
        ItemStack[] current = player.getInventory().getStorageContents();
        System.arraycopy(current, 0, kit, 0, 9);
        player.getInventory().setStorageContents(kit);
    }

    public boolean removeKit(Player player) {
        if (inventory == null) {
            return false;
        }
        // save hotbar + offhand
        ItemStack[] hotbar = new ItemStack[10];
        for (int i = 0; i < 9; i++) {
            hotbar[i] = player.getInventory().getItem(i);
        }
        hotbar[9] = player.getInventory().getItemInOffHand();
        artkitHotbars.put(player.getUniqueId(), hotbar);
        // clear item on cursor
        player.getOpenInventory().setCursor(null);
        
        player.getInventory().setContents(inventory);
        inventory = null;
        return true;
    }

    /**
     * Clear a players hotbar save. For instance on logout.
     * 
     * @param player The player to remove.
     */
    public static void clearHotbar(Player player) {
        artkitHotbars.remove(player.getUniqueId());
    }

    /**
     * @return True if the artsession has the artkit in use.
     */
    public boolean isInArtKit() {
        return this.inventory != null;
    }

    public Easel getEasel() {
        return easel;
    }

    public void end(Player player) throws SQLException, IOException {
        try {
            //player.leaveVehicle();
			player.teleport(player.getLocation().add(0, 0.25, 0 ));
            removeKit(player);
            easel.removeUser();
            canvas.stop();
            persistMap(true);
            active = false;
        } catch (Exception e) {
            player.sendMessage("Error saving painting on easel. Check logs for more details.");
            ArtMap.instance().getLogger().log(Level.SEVERE, "Error saving painting on easel.", e);
        }
        // todo map renderer getting killed after save
    }

    public void persistMap(boolean resetRenderer) throws SQLException, IOException, NoSuchFieldException,
            IllegalAccessException {
        if (!dirty) return; //no caching required
        byte[] mapData = canvas.getMap();
        map.setMap(mapData, resetRenderer);
        ArtMap.instance().getArtDatabase().saveInProgressArt(this.map, mapData);
        dirty = false;
    }

    public boolean isActive() {
        return active;
    }

    void setActive(boolean active) {
        this.active = active;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    void sendMap(Player player) {
        if (dirty) map.update(player);
    }

    /**
     * Clear the current map on the easel.
     * 
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws IOException
     * @throws SQLException
     */
    public void clearMap() throws NoSuchFieldException, IllegalAccessException, SQLException, IOException {
        canvas.clear();
        this.persistMap(true);
       //map.clear();
    }

    /* Artkit */
    private static WeakReference<List<ItemStack[]>> kitReference = new WeakReference<>(new LinkedList<>());

    private int numPages() {
        int numDyes = ArtMap.instance().getDyePalette().getDyes(DyeType.DYE).length;
        return (int) Math.ceil(numDyes / 18d);
    }

	// 27 inv slots + 9 hotbar slots = 36 slots
	private ItemStack[] getArtKit(int page) {
		// check the cache
		if (kitReference.get() != null && !kitReference.get().isEmpty()) {
			return kitReference.get().get(page).clone();
		}
		synchronized(kitReference) {
			if (kitReference.get() != null && !kitReference.get().isEmpty()) {
				return kitReference.get().get(page).clone();
			}
			kitReference = new WeakReference<>(new LinkedList<>());
			Palette palette = ArtMap.instance().getDyePalette();
			int numDyes = palette.getDyes(DyeType.DYE).length;
			int pages = (int) Math.ceil(numDyes / 18d);
			for (int pg = 0; pg < pages; pg++) {
				ItemStack[] itemStack = new ItemStack[36]; // 27 inv slots
				Arrays.fill(itemStack, new ItemStack(Material.AIR));

				for (int j = 0; j < 18; j++) {
					if (((pg * 18) + j) >= numDyes) {
						break;
					}
					ArtDye dye = palette.getDyes(DyeType.DYE)[(pg * 18) + j];
					itemStack[j + 9] = ItemUtils.addKey(dye.toItem(), ArtItem.KIT_KEY);
				}

				// if not first page add back button
				if (pg != 0) {
					ItemStack back = new ItemStack(Material.MAGENTA_GLAZED_TERRACOTTA);
					ItemMeta meta = back.getItemMeta();
					meta.setDisplayName(Lang.ARTKIT_PREV.get());
					meta.setLore(Arrays.asList("Artkit:Back"));
					back.setItemMeta(meta);
					itemStack[27] = back;
				}
				// if not last page add next button
				if (pg < pages - 1) {
					ItemStack next = new ItemStack(Material.MAGENTA_GLAZED_TERRACOTTA);
					ItemMeta meta = next.getItemMeta();
					meta.setDisplayName(Lang.ARTKIT_NEXT.get());
					meta.setLore(Arrays.asList("Artkit:Next"));
					next.setItemMeta(meta);
					itemStack[35] = next;
				}

				itemStack[29] = ArtMaterial.FEATHER.getItem();
				itemStack[30] = ArtMaterial.COAL.getItem();
				itemStack[31] = ArtMaterial.COMPASS.getItem();
				itemStack[32] = ArtMaterial.PAINTBUCKET.getItem();
				itemStack[33] = ArtMaterial.SPONGE.getItem();
				if(!ArtMap.instance().getConfiguration().DISABLE_PAINTBRUSH) {
					itemStack[34] = ArtMaterial.PAINT_BRUSH.getItem();
				}
				kitReference.get().add(itemStack);
			}
		}

		return kitReference.get().get(page).clone();
    }
}
