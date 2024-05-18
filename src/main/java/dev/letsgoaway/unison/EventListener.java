package dev.letsgoaway.unison;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class EventListener implements Listener {
    private HashMap<UUID, Material> lastEaten = new HashMap<>();

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
        try {
            if (ev.getInventory().getItem(0).getItemMeta().getLore().get(0).equals("Cancelling shield...")) {
                ev.setCancelled(true);
                Tick.runIn(1L,()->{
                   if (ev.getWhoClicked() instanceof Player player){
                       player.updateInventory();
                   }
                });
                return;
            }
        } catch (NullPointerException ignored) {
        }
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
                if (ev.getWhoClicked() instanceof Player player) {
                    player.updateInventory();
                }
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent ev) {
        if (Config.bedrockOffhandItemsOnly
                && ev.getInventory().getType().equals(InventoryType.CRAFTING)
                && !bedrockAllowed.contains(ev.getOldCursor().getType())) {
            ev.getRawSlots().forEach((slot) -> {
                if (slot == 45) {
                    ev.setCancelled(true);
                }
            });
        }
    }

    public void stopShieldBlocking(Player player) {

    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent ev) {
        if (!Config.blockWhenSneaking){
            return;
        }
        if (ev.isSneaking()) {
            ev.getPlayer().setCooldown(Material.SHIELD, 0);
        } else {
            ev.getPlayer().setCooldown(Material.SHIELD, 999999999);
            if (ev.getPlayer().isBlocking()
                    || ev.getPlayer().getInventory().getItemInOffHand().getType().equals(Material.SHIELD)
                    || ev.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.SHIELD)
            ) {
                Inventory inventory = Bukkit.createInventory(null, InventoryType.STONECUTTER, " ");
                ItemStack itemStack = new ItemStack(Material.COBBLED_DEEPSLATE);
                ItemMeta meta = itemStack.getItemMeta();
                if (meta == null)
                    return;
                meta = meta.clone();
                meta.setLore(List.of("Cancelling shield..."));
                itemStack.setItemMeta(meta);
                inventory.addItem(itemStack);
                ev.getPlayer().openInventory(inventory);
                Tick.runIn(0L, () -> {
                    ev.getPlayer().closeInventory();
                });
            }
        }
    }

    private void disconnect(UUID player) {
        lastEaten.remove(player);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent ev) {
        disconnect(ev.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent ev) {
        disconnect(ev.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent ev) {
        lastEaten.putIfAbsent(ev.getPlayer().getUniqueId(), ev.getItem().getType());
        lastEaten.replace(ev.getPlayer().getUniqueId(), ev.getItem().getType());
    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent ev) {
        if (!Config.bedrockConsumableEffects) {
            return;
        }
        if (!ev.getCause().equals(EntityPotionEffectEvent.Cause.FOOD) && !ev.getCause().equals(EntityPotionEffectEvent.Cause.TOTEM)) {
            return;
        }
        if (ev.getEntity() instanceof Player player) {
            Material lastFood = lastEaten.get(player.getUniqueId());
            PotionEffect effect = ev.getNewEffect();
            if (effect == null) {
                return;
            }
            Unison.plugin.getLogger().info(effect.toString());
            PotionEffectType type = effect.getType();
            int duration = effect.getDuration();
            int amplifier = effect.getAmplifier();
            boolean ambient = effect.isAmbient();
            boolean particles = effect.hasParticles();
            boolean icon = effect.hasIcon();

            if (ev.getCause().equals(EntityPotionEffectEvent.Cause.FOOD)) {
                if (lastFood.equals(Material.ENCHANTED_GOLDEN_APPLE)) {
                    if (type.equals(PotionEffectType.REGENERATION) && duration == 400 && amplifier == 1) {
                        ev.setCancelled(true);
                        player.addPotionEffect(
                                new PotionEffect(PotionEffectType.REGENERATION, 600, 4, ambient, particles, icon)
                        );
                    }
                }
                /*
                if (lastFood.equals(Material.SUSPICIOUS_STEW)) {
                    if (type.equals(PotionEffectType.FIRE_RESISTANCE) && duration == 400 && amplifier == 1) {
                        ev.setCancelled(true);
                        player.addPotionEffect(
                                new PotionEffect(PotionEffectType.REGENERATION, 600, 4, ambient, particles, icon)
                        );
                    }
                }
                */
            } else if (ev.getCause().equals(EntityPotionEffectEvent.Cause.TOTEM)) {
                if (type.equals(PotionEffectType.REGENERATION) && duration == 80 && amplifier == 1) {
                    ev.setCancelled(true);
                    player.addPotionEffect(
                            new PotionEffect(PotionEffectType.REGENERATION, 800, amplifier, ambient, particles, icon)
                    );
                }
            }
        }
    }
}
