package org.windy.coreProtectModFix;

import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.Prefix;

public class CoreProtectModFix extends JavaPlugin {
    private static CoreProtectModFix instance;
    private boolean debug;
    private boolean queryMode;
    private BlockInteractionListener blockInteractionListener;
    private String Prefix;
    private int Inedx;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        debug = getConfig().getBoolean("debug", false); // 默认开启 debug 模式
        Prefix = getConfig().getString("Prefix","&f---- &b\uD83D\uDD0D 交互查询系统 &f----");
        Inedx = getConfig().getInt("index",5);
        queryMode = false;
        // Initialize data storage
        DataStorage.init();
        // Register command
        this.getCommand("comod").setExecutor(new CommandHandler());
        // Register events
        blockInteractionListener = new BlockInteractionListener();
        getServer().getPluginManager().registerEvents(blockInteractionListener, this);

        String version = this.getDescription().getVersion();
        String serverName = this.getServer().getName();
        this.getServer().getConsoleSender().sendMessage(Texts.logo);
        this.getServer().getConsoleSender().sendMessage("v"+"§a" + version + "运行环境：§e " + serverName + "\n");


        if (debug) {
            getLogger().info("调教模式已开启");
        }
    }

    @Override
    public void onDisable() {
        // Save any necessary data before the plugin is disabled
        DataStorage.saveData();
        this.getServer().getConsoleSender().sendMessage(Texts.logo);
        getLogger().info("已卸载，感谢使用！");
    }

    public static CoreProtectModFix getInstance() {
        return instance;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
        getConfig().set("debug", debug);
        saveConfig();
    }

    public boolean isQueryMode() {
        return queryMode;
    }

    public void setQueryMode(boolean queryMode) {
        this.queryMode = queryMode;
    }

    public void toggleQueryMode() {
        setQueryMode(!isQueryMode());
    }

    public BlockInteractionListener getBlockInteractionListener() {
        return blockInteractionListener;
    }

    public String prefix() {
        return Prefix;
    }
    public int index() {
        return Inedx;
    }
}