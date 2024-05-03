package me.enchan.miners_tools.recursive_breaker;

import java.util.List;

import org.bukkit.block.Block;

//TODO: interfaceじゃなくて普通に継承でもよいかも?
/**
 * 連鎖破壊プロバイダ
 */
public interface ChainDestructionProvider {

    /**
     * あるブロックを起点に、周囲の連鎖破壊可能なブロックのリストを返す
     * 
     * @param start
     * @return
     */
    public List<Block> getNearbyCandidates(Block start);

}
