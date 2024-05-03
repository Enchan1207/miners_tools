package me.enchan.miners_tools;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import me.enchan.miners_tools.recursive_breaker.DestructionProviderFactory;
import me.enchan.miners_tools.recursive_breaker.ChainDestructionProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.bukkit.Material;

/**
 * プレイヤーの操作時に発生したイベントを処理するリスナ
 */
class PlayerInteractionListener implements Listener {

    /** 連鎖破壊を行うかどうか */
    boolean isEnabled = false;
    // TODO: リスナ初期化時にDI

    /**
     * プレイヤーがブロックを破壊したとき
     * 
     * @param event
     */
    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        // オフになっているなら何もしない
        if (!this.isEnabled) {
            return;
        }

        // 起点となるブロックと破壊に使用したツールを取得し、適用可能な連鎖破壊プロバイダを探す
        Block baseBlock = event.getBlock();
        ItemStack usedTool = event.getPlayer().getInventory().getItemInMainHand();
        Optional<ChainDestructionProvider> optionalProvider = DestructionProviderFactory.queryProvider(baseBlock,
                usedTool);
        if (optionalProvider.isEmpty()) {
            return;
        }
        ChainDestructionProvider destructionProvider = optionalProvider.get();

        // デフォルトイベントをキャンセルして連鎖破壊開始
        event.setCancelled(true);
        List<Block> candidates = new ArrayList<>();
        candidates.add(baseBlock);
        int brokenBlockCount = 1;
        while (candidates.size() > 0) {
            // 候補リストからひとつ取り出して破壊
            Block startBlock = candidates.remove(0);
            startBlock.breakNaturally(usedTool);

            // 連鎖破壊候補を取得し、まとめて破壊
            List<Block> nearbyCandidates = destructionProvider.getNearbyCandidates(startBlock);
            nearbyCandidates.stream().forEach(block -> block.breakNaturally(usedTool));
            brokenBlockCount += nearbyCandidates.size();

            // 候補リストの末尾に追加
            candidates.addAll(candidates.size(), nearbyCandidates);
        }

        // ツールの耐久を減らす
        Damageable toolMetadata = (Damageable) usedTool.getItemMeta();
        int currentDamage = toolMetadata.getDamage();
        toolMetadata.setDamage(currentDamage + brokenBlockCount);
        usedTool.setItemMeta(toolMetadata);

        // 必要に応じて経験値を与える
        event.getPlayer().giveExp(event.getExpToDrop() * brokenBlockCount);
    }

    /**
     * プレイヤーが右クリックしたとき
     * 
     * @param event
     */
    @EventHandler
    public void onPlayerInteractToVoid(PlayerInteractEvent event) {
        // 虚空に向かって右クリックした場合のみ
        if (event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }

        // オフハンドに何も持っていない場合のみ
        if (event.getPlayer().getInventory().getItemInOffHand().getType() != Material.AIR) {
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
