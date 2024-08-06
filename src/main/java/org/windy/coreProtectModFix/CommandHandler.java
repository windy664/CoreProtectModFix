package org.windy.coreProtectModFix;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        CoreProtectModFix plugin = CoreProtectModFix.getInstance();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (command.getName().equalsIgnoreCase("comod")) {
                if (args.length > 0) {
                    if (args[0].equalsIgnoreCase("i")) {
                        plugin.toggleQueryMode();
                        player.sendMessage(plugin.prefix()+" 交互检查器已" + (plugin.isQueryMode()? "启用" : "禁用") + "啦~");
                    }else if(args[0].equalsIgnoreCase("reload")){
                        if (!sender.hasPermission("CoreProtectModFix.reload")) {
                            sender.sendMessage(plugin.prefix()+"§c 你没有权限来执行这个指令!");
                        }
                        plugin.reloadConfig();
                        sender.sendMessage(plugin.prefix()+"§c插件已重载！");
                    }else if(args[0].equalsIgnoreCase("debug")){
                        sender.sendMessage(plugin.prefix()+"§c调教失败！请在配置文件里开启。");
                    } else{
                        sender.sendMessage(Texts.help);
                    }
                }
            }
        }
        return false;
    }
}