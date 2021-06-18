package world.bentobox.level.commands;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.level.Level;

import java.util.Arrays;
import java.util.List;

public class IslandValueCommand extends CompositeCommand {
    private final Level addon;

    public IslandValueCommand(Level addon, CompositeCommand parent) {
        super(parent, "value");
        this.addon = addon;
    }

    @Override
    public void setup() {
        this.setPermission("island.value");
        this.setDescription("island.value.description");
        this.setOnlyPlayer(true);
    }

    @Override
    public boolean execute(User user, String label, List<String> args) {
    	System.out.println("args: "+ args);
        Player player = user.getPlayer();
        PlayerInventory inventory = player.getInventory();
        if (!args.isEmpty() && args.get(0).equals("all")) {
            int total = Arrays.asList(inventory.getContents()).stream()
                    .filter(itemStack -> itemStack != null && itemStack.getType() != Material.AIR)
                    .filter(itemStack -> addon.getBlockConfig().getValue(getWorld(), itemStack.getType()) != null)
                    .mapToInt(itemStack -> addon.getBlockConfig().getValue(getWorld(), itemStack.getType()) * itemStack.getAmount())
                    .sum();
            user.sendMessage("island.value.success", "[value]", String.valueOf(total));
        } else if (!inventory.getItemInMainHand().getType().equals(Material.AIR)) {
            Material material = inventory.getItemInMainHand().getType();
            Integer value = addon.getBlockConfig().getValue(getWorld(), material);
            if (value != null) {
                user.sendMessage("island.value.success", "[value]", String.valueOf(value));
                double underWater = addon.getSettings().getUnderWaterMultiplier();
                if (underWater > 1.0) {
                    user.sendMessage("island.value.success-underwater", "[value]", (underWater * value) + "");
                }
            } else {
                user.sendMessage("island.value.no-value");
            }
        } else {
            user.sendMessage("island.value.empty-hand");
        }
        return true;
    }

}
