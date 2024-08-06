package org.windy.coreProtectModFix;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;


public class BlockInteractionListener implements Listener {
    private boolean debug;
    // 不再需要翻页相关的屌丝
    public BlockInteractionListener() {
        // 可以在这里做一些初始化工作
        debug = CoreProtectModFix.getInstance().isDebug();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (debug) {
            CoreProtectModFix.getInstance().getLogger().info(event.getPlayer().getName() + "破坏之" + event.getBlock().getLocation());
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (debug) {
            CoreProtectModFix.getInstance().getLogger().info(event.getPlayer().getName() + "把方块放到了" + event.getBlock().getLocation());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block != null) {
            FileConfiguration config = CoreProtectModFix.getInstance().getConfig();
            if (debug) {
                CoreProtectModFix.getInstance().getLogger().info("有玩家尝试与" + block.getType().name() + "产生交配，位置：" + block.getLocation());
            }
            // 检查方块是否在配置文件中
            if (config.getStringList("tracked-blocks").contains(block.getType().name())) {

                Player player = event.getPlayer();
                if(debug){
                CoreProtectModFix.getInstance().getLogger().info(player.getName() + " 与 " + block.getType().name() + " 发生交互");
                }
                // 查询模式
                if (CoreProtectModFix.getInstance().isQueryMode()) {
                    String record = DataStorage.getInteractionRecord(player, block);
                    if (!record.isEmpty()) {
                        // 对 record 进行数据处理
                        String[] lines = record.split(System.lineSeparator());
                        List<String> messages = new ArrayList<>();
                        for (String line : lines) {
                            // 提取时间和玩家名
                            String[] parts = line.split(" -- ");
                            long timestamp = Long.parseLong(parts[0]);
                            String playerName = parts[1].split(" ")[0]; // 提取玩家名
                            // 格式化消息
                            String message = "§7" + getTimeDifference(timestamp) + " 前 §c· §3" + playerName + " §7与 §3" + block.getType().name() + " §7发生交互";
                            messages.add(message);
                        }
                        // 按时间戳降序排序
                        Collections.sort(messages, new Comparator<String>() {
                            @Override
                            public int compare(String message1, String message2) {
                                // 提取时间部分并转换为 long 进行比较
                                String time1 = message1.split("§7")[1].split(" 前")[0].trim();
                                String time2 = message2.split("§7")[1].split(" 前")[0].trim();
                                try {
                                    long timestamp1 = Long.parseLong(time1.split(" ")[0]);
                                    long timestamp2 = Long.parseLong(time2.split(" ")[0]);
                                    return Long.compare(timestamp2, timestamp1);
                                } catch (NumberFormatException e) {
                                    // 处理时间格式错误的情况，例如打印错误日志或采取其他适当的措施
                                    CoreProtectModFix.getInstance().getLogger().severe("发生错误：" + message1 + " 或 " + message2);
                                    return 0; // 返回 0 表示不进行排序，或者根据你的需求进行其他处理
                                }
                            }
                        });
                        player.sendMessage(CoreProtectModFix.getInstance().prefix());
                        // 发送最近前 5 的记录
                        int numMessagesToSend = Math.min(CoreProtectModFix.getInstance().index(), messages.size());
                        for (int i = 0; i < numMessagesToSend; i++) {
                            player.sendMessage(messages.get(i));
                        }
                        // 上传经过处理的完整记录到粘贴网站
                        //      uploadToPastebin(processRecord(record));
                      //  player.sendMessage("完整版链接：" + uploadToPastebin(processRecord(record)));
                        player.sendMessage("完整版："+uploadToHastebin(processRecord(record)));
                        event.setCancelled(true);
                        if (debug) {
                            CoreProtectModFix.getInstance().getLogger().info("数据记录：" + messages.toString());
                        }
                    } else {
                        player.sendMessage("无记录");
                    }
                } else {
                    //记录数据
                    DataStorage.logBlockInteraction(player, block);
                }
            }
        }
    }

    private String getTimeDifference(long timestamp) {
        long currentTime = System.currentTimeMillis();
        long timeDiffInSeconds = (currentTime - timestamp) / 1000;
        String timeUnit;
        if (timeDiffInSeconds < 60) {
            timeUnit = "秒";
        } else if (timeDiffInSeconds < 3600) {
            timeUnit = "分";
            timeDiffInSeconds /= 60;
        } else if (timeDiffInSeconds < 86400) {
            timeUnit = "小时";
            timeDiffInSeconds /= 3600;
        } else if (timeDiffInSeconds < 2592000) {
            timeUnit = "天";
            timeDiffInSeconds /= 86400;
        } else if (timeDiffInSeconds < 7776000) {
            timeUnit = "月";
            timeDiffInSeconds /= 2592000;
        } else {
            timeUnit = "年";
            timeDiffInSeconds /= 7776000;
        }
        return timeDiffInSeconds + " " + timeUnit;
    }

    private String processRecord(String record) {
        // 对 record 进行处理，添加时间、玩家和方块的信息，并处理时间格式
        String[] lines = record.split(System.lineSeparator());
        StringBuilder processedRecord = new StringBuilder();
        for (String line : lines) {
            String[] parts = line.split(" -- ");
            long timestamp = Long.parseLong(parts[0]);
            String playerName = parts[1].split(" ")[0];
            String blockName = parts[1].split(" ")[3];

            // 处理时间格式
            String timeDifference = getTimeDifference(timestamp);

            processedRecord.append(timeDifference).append("前 - ").append(playerName).append(" 与 ").append(blockName).append(" 发生交互\n");
        }
        return processedRecord.toString();
    }

    private String uploadToHastebin(String content) {
        String pasteUrl = "https://paste.helpch.at/documents";
        try {
            URL url = new URL(pasteUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "text/plain");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = content.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            if (connection.getResponseCode() == 200) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String responseBody = scanner.useDelimiter("\\A").next();
                    // 解析返回的JSON，获取key
                    JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                    String key = jsonObject.get("key").getAsString();
                    return "https://paste.helpch.at/" + key;
                }
            } else {
                return "Error: " + connection.getResponseCode();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "无法连接：" + e.getMessage();
        }
    }
}