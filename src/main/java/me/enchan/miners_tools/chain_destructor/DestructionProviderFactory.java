package me.enchan.miners_tools.chain_destructor;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.inventory.ItemStack;

/**
 * 連鎖破壊プロバイダのファクトリ
 */
public final class DestructionProviderFactory {

    /**
     * ブロックとそれを破壊するのに用いるツールから、それに適用できる連鎖破壊プロバイダを探す
     * 
     * @param base     破壊予定のブロック
     * @param usedTool 破壊に用いるツール
     * @return 対応する連鎖破壊クラスが見つかればそのインスタンス、見つからなければnull
     */
    public static Optional<ChainDestructionProvider> queryProvider(Block base, ItemStack usedTool) {
        String baseBlokName = base.getType().name();
        String usedToolName = usedTool.getType().name();

        // ピッケルかつ鉱石 -> 採掘プロバイダ
        if (usedToolName.endsWith("_PICKAXE") && baseBlokName.endsWith("_ORE")) {
            return Optional.of(new ChainMiningProvider(base));
        }

        // 斧かつ原木 -> 伐採プロバイダ
        if (usedToolName.endsWith("_AXE") && baseBlokName.endsWith("_LOG")) {
            return Optional.of(new ChainFellingProvider(base));
        }

        // 作物かつ最大まで成長 -> 収穫プロバイダ
        if (Tag.CROPS.isTagged(base.getType())) {
            Ageable baseBlockData = (Ageable) base.getBlockData();
            if (baseBlockData != null && baseBlockData.getAge() == baseBlockData.getMaximumAge()) {
                return Optional.of(new ChainHarvestingProvider(base));
            }
        }

        return Optional.empty();
    }

}
