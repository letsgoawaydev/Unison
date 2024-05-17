package dev.letsgoaway.unison;

import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class EventListener implements Listener {
    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent ev) {
        if (Config.enableArmsOnArmorStands && ev.getEntity() instanceof ArmorStand armorStand) {
            armorStand.setArms(true);
        }
    }

    @EventHandler
    public void onQuickOffhandSwap(PlayerSwapHandItemsEvent ev) {
        ev.setCancelled(Config.disableOffhandKeybind);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent ev) {
        if (ev.getClick().equals(ClickType.SWAP_OFFHAND)) {
            ev.setCancelled(Config.disableOffhandKeybind);
        }
        if (ev.getClick().equals(ClickType.NUMBER_KEY)) {
            ev.setCancelled(Config.disableNumberKeyInventoryHotkey);
        }
    }
}
