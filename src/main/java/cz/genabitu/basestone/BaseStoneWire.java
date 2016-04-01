package cz.genabitu.basestone;

import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Random;

public class BaseStoneWire extends BlockRedstoneWire {
    public final String name = "baseStoneWire";
    public BaseStoneWire()
    {
        super();
        GameRegistry.registerBlock(this, name);
        setUnlocalizedName(name);
    }
    
    public Item getItemDropped(int metadata, Random random, int fortune)
    {
        return BaseStone.baseStoneDust;
    }
}
