package me.Fupery.ArtMap.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.api.Config.Lang;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.Easel.EaselEffect;
import me.Fupery.ArtMap.Easel.EaselEvent;
import me.Fupery.ArtMap.Easel.EaselPart;
import me.Fupery.ArtMap.Event.PlayerOpenMenuEvent;
import me.Fupery.ArtMap.Menu.Handler.MenuHandler;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import me.Fupery.ArtMap.api.Easel.ClickType;

class PlayerInteractEaselListener implements RegisteredListener {

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        callEaselEvent(player, event.getRightClicked(), event, isSneaking(player));
        checkPreviewing(player, event);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        Player player = event.getPlayer();
        callEaselEvent(player, event.getRightClicked(), event, ClickType.LEFT_CLICK);
        checkPreviewing(player, event);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        callEaselEvent(null, event.getRightClicked(), event, isSneaking(player));
        checkPreviewing(player, event);
    }


    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        callEaselEvent(event.getDamager(), event.getEntity(), event, ClickType.LEFT_CLICK);
    }

    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (event.getCause() == HangingBreakEvent.RemoveCause.ENTITY) {
            callEaselEvent(event.getRemover(), event.getEntity(), event, ClickType.LEFT_CLICK);
        }
    }

	@EventHandler
	public void onRightClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Action a = e.getAction();
		if ( a == Action.RIGHT_CLICK_AIR && e.getItem() != null
				&& (ArtMaterial.getCraftItemType(e.getItem()) == ArtMaterial.PAINT_BRUSH)) {
			ArtMap.instance().getScheduler().SYNC.run(() -> {
				PlayerOpenMenuEvent event = new PlayerOpenMenuEvent(p);
				Bukkit.getServer().getPluginManager().callEvent(event);
				MenuHandler menuHandler = ArtMap.instance().getMenuHandler();
				menuHandler.openMenu((p), menuHandler.MENU.HELP.get((p)));
			});
		}
	}

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        if (checkSignBreak(event.getBlock(), event)) {
            if (!checkIsPainting(event.getPlayer(), event)) {
                Lang.ActionBar.EASEL_HELP.send(event.getPlayer());
            }
        }
        checkIsPainting(event.getPlayer(), event);
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        checkSignBreak(event.getBlock(), event);
    }

    private void callEaselEvent(Entity clicker, Entity clicked, Cancellable event, ClickType click) {
        EaselPart part = EaselPart.getPartType(clicked);
        if (part == null || part == EaselPart.SEAT || part == EaselPart.MARKER) return;
        Easel easel = Easel.getEasel(clicked.getLocation(), part);
        if (easel == null) return;

        event.setCancelled(true);

        if (clicker == null || !(clicker instanceof Player)) return;
        Player player = (Player) clicker;

        boolean interactionAllowed = (click == ClickType.SHIFT_RIGHT_CLICK) ?
                ArtMap.instance().getCompatManager().checkBuildAllowed(player, clicked.getLocation()) :
                ArtMap.instance().getCompatManager().checkInteractAllowed(player, clicked, click);

        if (!interactionAllowed) {
            Lang.ActionBar.NO_PERM_ACTION.send(player);
            easel.playEffect(EaselEffect.USE_DENIED);
            return;
        }
        if (!checkIsPainting(player, event)) new EaselEvent(easel, click, player).callEvent();
    }

    private ClickType isSneaking(Player player) {
        return (player.isSneaking()) ? ClickType.SHIFT_RIGHT_CLICK :
                ClickType.RIGHT_CLICK;
    }

    private boolean checkIsPainting(Player player, Cancellable event) {
		if (player.isInsideVehicle() && ArtMap.instance().getArtistHandler().containsPlayer(player)) {
            event.setCancelled(true);
            return true;
        }
        return false;
    }

    private void checkPreviewing(Player player, Cancellable event) {
        if (ArtMap.instance().getPreviewManager().endPreview(player)) event.setCancelled(true);
    }

    private boolean checkSignBreak(Block block, Cancellable event) {

        if (block.getType() == ArtMap.instance().getBukkitVersion().getVersion().getWallSign()) {
            Sign sign = ((Sign) block.getState());

            if (sign.getLine(3).equals(EaselPart.ARBITRARY_SIGN_ID)) {

                if (ArtMap.instance().getEasels().containsKey(block.getLocation())
                        || Easel.checkForEasel(block.getLocation())) {
                    event.setCancelled(true);
                    return true;
                }
            }
        }
        return false;
    }

	@Override
    public void unregister() {
        PlayerInteractAtEntityEvent.getHandlerList().unregister(this);
        PlayerArmorStandManipulateEvent.getHandlerList().unregister(this);
        PlayerInteractEntityEvent.getHandlerList().unregister(this);
        EntityDamageByEntityEvent.getHandlerList().unregister(this);
        HangingBreakByEntityEvent.getHandlerList().unregister(this);
        BlockBreakEvent.getHandlerList().unregister(this);
        BlockPhysicsEvent.getHandlerList().unregister(this);
    }
}
