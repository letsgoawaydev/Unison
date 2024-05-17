package dev.letsgoaway.unison;

import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class EventListener implements Listener {
    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent ev) {
        if (Config.enableArmsOnArmorStands && ev.getEntity() instanceof ArmorStand armorStand) {
            armorStand.setArms(true);
        }
    }

    @EventHandler
    public void onQuickOffhandSwap(PlayerSwapHandItemsEvent ev) {
        ev.setCancelled(Config.disableOffhandKeybind || !bedrockAllowed.contains(Objects.requireNonNull(ev.getOffHandItem()).getType()));
    }

    public List<Material> bedrockAllowed = Arrays.asList(Material.SHIELD, Material.ARROW, Material.SPECTRAL_ARROW, Material.TIPPED_ARROW, Material.FIREWORK_ROCKET, Material.TOTEM_OF_UNDYING, Material.FILLED_MAP, Material.NAUTILUS_SHELL, Material.AIR, Material.VOID_AIR, Material.CAVE_AIR);

    @EventHandler
    public void onInventoryClick(InventoryClickEvent ev) {
        if (ev.getClick().equals(ClickType.SWAP_OFFHAND)) {
            ev.setCancelled(Config.disableOffhandKeybind);
            return;
        }
        if (ev.getClick().equals(ClickType.NUMBER_KEY)) {
            ev.setCancelled(Config.disableNumberKeyInventoryHotkey);
            return;
        }
        if (ev.getClick().equals(ClickType.SWAP_OFFHAND) && Config.bedrockOffhandItemsOnly) {
            ev.setCancelled(!bedrockAllowed.contains(Objects.requireNonNull(ev.getCurrentItem()).getType()));
        }
        if (Config.bedrockOffhandItemsOnly && !ev.isCancelled() && ev.getCursor() != null) {
            if (ev.getSlot() == 40 &&
                    (ev.getAction().name().contains("PLACE")
                    || ev.getAction().equals(InventoryAction.SWAP_WITH_CURSOR)
                    || ev.getAction().equals(InventoryAction.NOTHING)
                    || ev.getAction().equals(InventoryAction.UNKNOWN)
                    )
                    && ev.getInventory().getType().equals(InventoryType.CRAFTING)
                    && (!bedrockAllowed.contains(ev.getCursor().getType())
                    || !bedrockAllowed.contains(Objects.requireNonNull(ev.getCurrentItem()).getType()))) {
                ev.setCancelled(true);
                if (ev.getWhoClicked() instanceof Player player){
                    player.updateInventory();
                }
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent ev){
        if (Config.bedrockOffhandItemsOnly
                && ev.getInventory().getType().equals(InventoryType.CRAFTING)
                && !bedrockAllowed.contains(ev.getOldCursor().getType())){
            ev.getRawSlots().forEach((slot)->{
                if (slot == 45){
                    ev.setCancelled(true);
                }
            });
        }

    }
}
