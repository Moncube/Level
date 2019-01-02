package world.bentobox.level.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import world.bentobox.bentobox.api.events.island.IslandEvent.IslandCreatedEvent;
import world.bentobox.bentobox.api.events.island.IslandEvent.IslandResettedEvent;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.level.Level;
import world.bentobox.level.calculators.CalcIslandLevel;

/**
 * Listens for new islands and sets the level to zero automatically
 * @author tastybento
 *
 */
public class NewIslandListener implements Listener {

    private final Level addon;
    private final Map<Island, CalcIslandLevel> cil;

    /**
     * @param addon - addon
     */
    public NewIslandListener(Level addon) {
        this.addon = addon;
        cil = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onNewIsland(IslandCreatedEvent e) {
        cil.putIfAbsent(e.getIsland(), new CalcIslandLevel(addon, e.getIsland(), () -> zeroLevel(e.getIsland())));
        // Set deaths to zero just in case
        addon.getPlayers().setDeaths(e.getIsland().getWorld(), e.getOwner(), 0);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onNewIsland(IslandResettedEvent e) {
        cil.putIfAbsent(e.getIsland(), new CalcIslandLevel(addon, e.getIsland(), () -> zeroLevel(e.getIsland())));
        // Reset deaths
        if (addon.getSettings().isIslandResetDeathReset()) {
            addon.getPlayers().setDeaths(e.getIsland().getWorld(), e.getOwner(), 0);
        }
    }

    private void zeroLevel(Island island) {
        if (cil.containsKey(island)) {
            addon.setInitialIslandLevel(island, cil.get(island).getResult().getLevel());
            cil.remove(island);
        }
    }
}
