package org.windy.coreProtectModFix;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.ArrayList;

public class DataStorage {

    private static final String DATA_FILE = "block-interactions.log";

    public static void init() {
        File dataFile = new File(CoreProtectModFix.getInstance().getDataFolder(), DATA_FILE);
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void logBlockInteraction(Player player, Block block) {
        String record = System.currentTimeMillis() + " -- " + player.getName() + " interacted with " + block.getType().name() + " at " + block.getLocation();
        try {
            Files.write(Paths.get(CoreProtectModFix.getInstance().getDataFolder().toString(), DATA_FILE), (record + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getInteractionRecord(Player player, Block block) {
        List<String> lines = new ArrayList<>();
        try {
            lines = Files.readAllLines(Paths.get(CoreProtectModFix.getInstance().getDataFolder().toString(), DATA_FILE));
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder record = new StringBuilder();
        for (String line : lines) {
            if (line.contains(block.getLocation().toString())) {
                record.append(line).append(System.lineSeparator());
            }
        }
        return record.toString();
    }

    public static void saveData() {
        // Implement any necessary data saving logic here
    }
}
