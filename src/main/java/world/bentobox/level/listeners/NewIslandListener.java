package world.bentobox.level.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import world.bentobox.level.Level;
import world.bentobox.level.calculators.CalcIslandLevel;
import world.bentobox.bentobox.api.events.island.IslandEvent.IslandCreatedEvent;
import world.bentobox.bentobox.api.events.island.IslandEvent.IslandResettedEvent;
import world.bentobox.bentobox.api.events.team.TeamEvent.TeamJoinEvent;
import world.bentobox.bentobox.database.objects.Island;

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
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onNewIsland(IslandResettedEvent e) {
        cil.putIfAbsent(e.getIsland(), new CalcIslandLevel(addon, e.getIsland(), () -> zeroLevel(e.getIsland())));
    }


    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onTeamJoin(TeamJoinEvent event)
    {
        if (this.addon.getSettings().isTeamJoinDeathReset())
        {
            this.cil.putIfAbsent(event.getIsland(),
                new CalcIslandLevel(this.addon,
                    event.getIsland(),
                    () -> zeroLevel(event.getIsland())));

            this.addon.getPlayers().setDeaths(event.getIsland().getWorld(),
                event.getOwner(),
                0);
        }
    }


    private void zeroLevel(Island island) {
        if (cil.containsKey(island)) {
            addon.setInitialIslandLevel(island, cil.get(island).getResult().getLevel());
            cil.remove(island);
        }
    }
}
