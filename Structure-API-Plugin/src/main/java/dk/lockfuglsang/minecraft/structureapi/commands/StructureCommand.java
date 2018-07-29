package dk.lockfuglsang.minecraft.structureapi.commands;

import dk.lockfuglsang.minecraft.command.AbstractCommand;
import dk.lockfuglsang.minecraft.command.BaseCommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Map;

import static dk.lockfuglsang.minecraft.po.I18nUtil.marktr;
import static dk.lockfuglsang.minecraft.po.I18nUtil.tr;

public class StructureCommand extends BaseCommandExecutor {
    public StructureCommand() {
        super("structure", "structure.command", marktr("main structure command"));
        add(new AbstractCommand("load", "structure.load", "name", marktr("loads a predefined structure")) {
            @Override
            public boolean execute(CommandSender commandSender, String s, Map<String, Object> map, String... strings) {
                commandSender.sendMessage(tr("hello world"));
                return true;
            }
        });
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        return super.onCommand(sender, command, alias, args);
    }
}
