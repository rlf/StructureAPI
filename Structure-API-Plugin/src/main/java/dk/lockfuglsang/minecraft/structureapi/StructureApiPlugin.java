package dk.lockfuglsang.minecraft.structureapi;

import dk.lockfuglsang.minecraft.structureapi.commands.StructureCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class StructureApiPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("structure").setExecutor(new StructureCommand(this, () -> getAPI()));
    }

    @Override
    public void onDisable() {
        // Do nothing
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return super.onCommand(sender, command, label, args);
    }

    public StructureApi getAPI() {
        return StructureUtil.getAPI();
    }
}
