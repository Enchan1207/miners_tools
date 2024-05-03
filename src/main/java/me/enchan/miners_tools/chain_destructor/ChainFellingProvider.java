package me.enchan.miners_tools.chain_destructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * 連鎖伐採プロバイダ
 */
public class ChainFellingProvider implements ChainDestructionProvider {

    /** 起点ブロックの種類 */
    private final Material baseMaterial;

    /** 起点ブロックの座標 */
    private final Location baseLocation;

    /** 切り始めた木に隣接する葉 */
    private Material adjoinedLeaf;

    ChainFellingProvider(Block baseBlock) {
        this.baseMaterial = baseBlock.getType();
        this.baseLocation = baseBlock.getLocation();
        this.adjoinedLeaf = null;
    }

    @Override
    public List<Block> getNearbyCandidates(Block start) {
        // 破壊候補を生成して返す
        double maxHeightDiff = 16;
        double maxDistance = 4;
        List<Block> candidates = Arrays.asList(BlockFace.values()).stream()
                .map(face -> start.getRelative(face))
                .map(block -> {
                    // 葉ブロックが設定されていない場合は探す
                    Material material = block.getType();
                    if (adjoinedLeaf == null && material.name().endsWith("_LEAVES")) {
                        adjoinedLeaf = material;
                        Bukkit.getLogger().info("adjoined leaf: " + material.name());
                    }
                    return block;
                })
                .filter(block -> {
                    Location location = block.getLocation();

                    // 樹木は上下方向に伸びるので、連鎖判定範囲を円筒形にする
                    double diffX = location.getX() - baseLocation.getX();
                    double diffZ = location.getZ() - baseLocation.getZ();
                    double horizontalDistance = Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffZ, 2));
                    double verticalDistance = Math.abs(location.getY() - baseLocation.getY());

                    return horizontalDistance <= maxDistance && verticalDistance <= maxHeightDiff;
                })
                .filter(block -> {
                    // 起点と同じブロックか、始点に隣接する葉ブロックを選択
                    Material material = block.getType();
                    return material == baseMaterial || material == adjoinedLeaf;
                })
                .collect(Collectors.toList());

        return candidates;
    }

}
