//
// ブロック破壊時の処理
//
package me.enchan.miners_tools;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 * ブロック破壊イベントリスナ
 */
class BlockBreakListener implements Listener {

    boolean isEnabled = false;

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        // オフになっているなら何もしない
        if (!this.isEnabled) {
            return;
        }

        // 起点となるブロックの情報を取得
        Block baseBlock = event.getBlock();
        Material baseBlockMaterial = baseBlock.getType();
        Location baseBlockLocation = baseBlock.getLocation();

        // 破壊対象でないなら戻る
        if (!baseBlockMaterial.name().endsWith("_ORE")) {
            return;
        }

        // デフォルトの動作を遮断
        event.setCancelled(true);

        // 破壊に使用したツールを取得
        ItemStack usedTool = event.getPlayer().getInventory().getItemInMainHand();

        // 探索開始
        List<Block> breakCandidates = new ArrayList<>();
        double maxDistance = 10;
        int brokenBlockCount = 1;
        List<BlockFace> allFaces = Arrays.asList(BlockFace.values());
        breakCandidates.add(baseBlock);
        while (breakCandidates.size() > 0) {
            // 候補リストからひとつ取り出して破壊
            Block candidateBlock = breakCandidates.remove(0);
            candidateBlock.breakNaturally(usedTool);

            // 取り出した候補の周辺にある、起点と同種のブロックを検索
            // 無限ループを避けるため、起点からの距離が一定以上のものは含めない
            List<Block> nearbySameBlocks = allFaces.stream()
                    .map(face -> candidateBlock.getRelative(face))
                    .filter(block -> block.getType() == baseBlockMaterial)
                    .filter(block -> block.getLocation().distance(baseBlockLocation) <= maxDistance)
                    .collect(Collectors.toList());

            // 検索したブロックをまとめて破壊し、候補リストの末尾に追加
            nearbySameBlocks.stream().forEach(block -> block.breakNaturally(usedTool));
            breakCandidates.addAll(breakCandidates.size(), nearbySameBlocks);

            // 破壊カウントを加算
            brokenBlockCount += nearbySameBlocks.size();
        }

        // ツールの耐久を減らす
        Damageable toolMetadata = (Damageable) usedTool.getItemMeta();
        int currentDamage = toolMetadata.getDamage();
        toolMetadata.setDamage(currentDamage + brokenBlockCount);
        usedTool.setItemMeta(toolMetadata);
    }

    @EventHandler
    public void onPlayerInteractToVoid(PlayerInteractEvent event) {
        // 虚空に向かって右クリックした場合のみ
        if (event.getClickedBlock() != null) {
            return;
        }

        // メインハンドがツールの場合のみ
        String mainhandItemName = event.getPlayer().getInventory().getItemInMainHand().getType().name();
        String suffixes[] = { "_AXE", "_HOE", "_PICKAXE", "_SHOVEL" };
        if (!Arrays.asList(suffixes).stream().anyMatch(suffix -> mainhandItemName.endsWith(suffix))) {
            return;
        }

        // モードを切り替える
        this.isEnabled = !this.isEnabled;

        // ユーザに通知
        String message = this.isEnabled ? "enabled" : "disabled";
        event.getPlayer().sendMessage("Miners tools: " + message);
    }

}
