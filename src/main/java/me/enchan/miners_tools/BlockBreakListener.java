//
// ブロック破壊時の処理
//
package me.enchan.miners_tools;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.Bukkit;

/**
 * ブロック破壊イベントリスナ
 */
class BlockBreakListener implements Listener {

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Block target = event.getBlock();
        Bukkit.getLogger().info(target.getType().toString());
    }

}
