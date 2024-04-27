package ch.ksrminecraft.entityplugin.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpwanListener implements Listener {

    private final Map<UUID, BossBar> bossBars = new HashMap<>();

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {

        Player player = e.getPlayer();

        // Überprüfe, ob die Aktion ein Rechtsklick ist. Achtung: Der Rechtsklick geht im Spiel nur mit einem Item in der Hand
        if (e.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }

        // Hole die aktuelle Blickrichtung des Spielers
        Vector direction = player.getLocation().getDirection();

        // Berechne die neue Position, 10 Blöcke vor der aktuellen Position in Blickrichtung
        Location spawnLocation = player.getLocation().add(direction.multiply(10));

        // Setze die Y-Koordinate so, dass der Giant auf dem Boden spawnt
        spawnLocation.setY(player.getWorld().getHighestBlockYAt(spawnLocation));

        // Spawn den Giant an der berechneten Position
        Entity entity = player.getWorld().spawnEntity(spawnLocation, EntityType.GIANT);

        // Den Giant bearbeiten (Eigenschaften)
        entity.setGravity(false);
        entity.setGlowing(true);
        entity.setInvulnerable(true);
        entity.setCustomName("Endboss");
        entity.setCustomNameVisible(true);

        // Die Entität in einen Giant-Zombie umwandeln
        Giant giant = (Giant) entity;

        // Erhöhe das maximale Gesundheitsattribut und setze die Gesundheit
        giant.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100.0);
        giant.setHealth(100.0);

        // Erstelle eine Boss-Bar für den Giant
        BossBar bossBar = Bukkit.createBossBar("Endboss", BarColor.RED, BarStyle.SOLID);
        bossBar.addPlayer(player);
        bossBar.setVisible(true);

        bossBars.put(giant.getUniqueId(), bossBar);
    }

    // Aktualisiere die Boss-Bar bei jedem Schaden, den der Giant nimmt
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Giant && bossBars.containsKey(entity.getUniqueId())) {
            Giant giant = (Giant) entity;
            BossBar bossBar = bossBars.get(entity.getUniqueId());
            double health = giant.getHealth() - event.getFinalDamage();
            double maxHealth = giant.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
            bossBar.setProgress(Math.max(health / maxHealth, 0.0));
        }
    }

    // Lösche die Boss-Bar, wenn der Giant gestorben ist
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        UUID entitiyID = event.getEntity().getUniqueId();
        BossBar bossBar = bossBars.remove(entitiyID);
        if (bossBar != null) {
            bossBar.setVisible(false);
            bossBar.removeAll();
        }
    }
}