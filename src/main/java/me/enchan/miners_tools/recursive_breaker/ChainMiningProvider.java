package me.enchan.miners_tools.recursive_breaker;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * 連鎖採掘プロバイダ
 */
public class ChainMiningProvider implements ChainDestructionProvider {

    /** 起点ブロックの種類 */
    private final Material baseMaterial;

    /** 起点ブロックの座標 */
    private final Location baseLocation;

    ChainMiningProvider(Block baseBlock) {
        this.baseMaterial = baseBlock.getType();
        this.baseLocation = baseBlock.getLocation();
    }

    @Override
    public List<Block> getNearbyCandidates(Block start) {
        // 開始ブロック周辺で起点ブロックから一定距離にある同種のブロックを全てリストアップ
        double maxDistance = 24;
        return Arrays.asList(BlockFace.values())
                .stream()
                .map(face -> start.getRelative(face))
                .filter(block -> block.getType() == baseMaterial)
                .filter(block -> block.getLocation().distance(baseLocation) <= maxDistance)
                .collect(Collectors.toList());
    }

}
