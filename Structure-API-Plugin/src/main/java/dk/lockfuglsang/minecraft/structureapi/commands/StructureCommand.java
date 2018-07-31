package dk.lockfuglsang.minecraft.structureapi.commands;

import dk.lockfuglsang.minecraft.command.AbstractCommand;
import dk.lockfuglsang.minecraft.command.BaseCommandExecutor;
import dk.lockfuglsang.minecraft.command.DocumentCommand;
import dk.lockfuglsang.minecraft.command.completion.AbstractTabCompleter;
import dk.lockfuglsang.minecraft.file.FileUtil;
import dk.lockfuglsang.minecraft.structureapi.Structure;
import dk.lockfuglsang.minecraft.structureapi.StructureApi;
import dk.lockfuglsang.minecraft.structureapi.StructureUtil;
import dk.lockfuglsang.minecraft.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static dk.lockfuglsang.minecraft.po.I18nUtil.marktr;
import static dk.lockfuglsang.minecraft.po.I18nUtil.tr;

public class StructureCommand extends BaseCommandExecutor {
    private JavaPlugin plugin;

    public StructureCommand(JavaPlugin plugin, Supplier<StructureApi> api) {
        super("structure", "structure.command", marktr("main structure command"));
        this.plugin = plugin;
        add(new AbstractCommand("paste", "structure.paste", "name ?entities", marktr("loads a predefined structure")) {
            @Override
            public boolean execute(CommandSender commandSender, String alias, Map<String, Object> map, String... args) {
                if (!(commandSender instanceof Player)) {
                    commandSender.sendMessage(tr("Unable to execute this command as anything but a player"));
                    return false;
                }
                Player player = (Player) commandSender;
                String name = args.length > 0 ? args[0] : null;
                Location origin = player.getLocation();
                String mirror = "NONE";
                String rotate = "NONE";
                boolean includeEntities = args.length > 1 && args[1].equalsIgnoreCase("true");
                Structure structure = api.get().readStructure(player.getWorld(), name);
                if (api.get().paste(origin, structure, mirror, rotate, includeEntities)) {
                    commandSender.sendMessage(tr("Loaded structure {0} into position {1}", name, LocationUtil.asString(origin)));
                } else {
                    commandSender.sendMessage(tr("Failed to paste structure {0}", name));
                }
                return true;
            }
        });
        add(new AbstractCommand("paste-file", "structure.paste-file", "filename ?entities", marktr("loads a structure from a file")) {
            @Override
            public boolean execute(CommandSender commandSender, String alias, Map<String, Object> map, String... args) {
                if (!(commandSender instanceof Player)) {
                    commandSender.sendMessage(tr("Unable to execute this command as anything but a player"));
                    return false;
                }
                Player player = (Player) commandSender;
                String filename = args.length > 0 ? args[0] : null;
                Location origin = player.getLocation();
                String mirror = "NONE";
                String rotate = "NONE";
                boolean includeEntities = args.length > 1 && args[1].equalsIgnoreCase("true");
                try {
                    InputStream in = getClass().getClassLoader().getResourceAsStream(filename);
                    if (in == null) {
                        if (new File(plugin.getDataFolder(), filename).exists()) {
                            in = new FileInputStream(new File(plugin.getDataFolder(), filename));
                        }
                    }
                    Structure structure = api.get().readStructure(origin.getWorld(), FileUtil.getBasename(filename), in);
                    if (api.get().paste(origin, structure, mirror, rotate, includeEntities)) {
                        commandSender.sendMessage(tr("Loaded structure {0} into position {1}", filename, LocationUtil.asString(origin)));
                    } else {
                        commandSender.sendMessage(tr("Failed to paste structure {0}", filename));
                    }
                } catch (Exception e) {
                    commandSender.sendMessage(tr("Failed to paste structure {0}: {1}", filename, e.getMessage()));
                }
                return true;
            }
        });
        add(new AbstractCommand("info", "structure.info", "filename", marktr("shows information about a structure")) {
            @Override
            public boolean execute(CommandSender commandSender, String alias, Map<String, Object> map, String... args) {
                String filename = args.length > 0 ? args[0] : null;
                try (InputStream in = findInputStream(filename)) {
                    World world = commandSender.getServer().getWorlds().get(0);
                    Structure structure = api.get().readStructure(world, FileUtil.getBasename(filename), in);
                    if (structure != null) {
                        commandSender.sendMessage(tr("Structure found {0}", structure));
                    } else {
                        commandSender.sendMessage(tr("Failed to locate structure {0}", filename));
                    }
                } catch (Exception e) {
                    commandSender.sendMessage(tr("Failed to paste structure {0}: {1}", filename, e.getMessage()));
                }
                return true;
            }
        });
        add(new AbstractCommand("save", "structure.save", "name radius ?entitites", marktr("saves a structure")) {
            @Override
            public boolean execute(CommandSender commandSender, String alias, Map<String, Object> map, String... args) {
                if (!(commandSender instanceof Player)) {
                    commandSender.sendMessage(tr("Unable to execute this command as anything but a player"));
                    return false;
                }
                if (args.length < 2) {
                    return false;
                }
                Player player = (Player) commandSender;
                String name = args.length > 0 ? args[0] : null;
                Location origin = player.getLocation();
                int radius = args.length > 1 && args[1].matches("[0-9]+") ? Integer.parseInt(args[1], 10) : 1;
                String author = player.getName();
                boolean includeEntities = args.length > 2 && args[2].equalsIgnoreCase("true");
                Structure structure = api.get().copy(origin, radius, name, author, includeEntities);
                if (api.get().saveStructure(origin.getWorld(), structure)) {
                    commandSender.sendMessage(tr("Successfully saved structure {0}", name));
                } else {
                    commandSender.sendMessage(tr("Failed to save structure {0}", name));
                }
                return true;
            }
        });
        add(new AbstractCommand("debug", "structure.debug", "nmsclass", marktr("prints out debug-information about the NMS classes in play")) {
            @Override
            public boolean execute(CommandSender commandSender, String s, Map<String, Object> map, String... args) {
                if (args.length == 1) {
                    commandSender.sendMessage(StructureUtil.dumpMethods(args[0]).toArray(new String[0]));
                    return true;
                }
                return false;
            }
        });
        add(new DocumentCommand(plugin, "doc", "structure.doc"));
        addTab("nmsclass", new AbstractTabCompleter() {
            @Override
            protected List<String> getTabList(CommandSender commandSender, String s) {
                return Arrays.asList("MinecraftServer", "DefinedStructure", "DefinedStructureManager");
            }
        });
    }

    private InputStream findInputStream(String filename) throws FileNotFoundException {
        InputStream in = getClass().getClassLoader().getResourceAsStream(filename);
        if (in == null) {
            if (new File(plugin.getDataFolder(), filename).exists()) {
                in = new FileInputStream(new File(plugin.getDataFolder(), filename));
            }
        }
        return in;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        return super.onCommand(sender, command, alias, args);
    }
}
