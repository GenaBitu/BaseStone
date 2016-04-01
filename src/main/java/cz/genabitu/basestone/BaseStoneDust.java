package cz.genabitu.basestone;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BaseStoneDust extends Item {
    public final String name = "baseStoneDust";

    public BaseStoneDust()
    {
        GameRegistry.registerItem(this, name);
        setMaxStackSize(64);
        setCreativeTab(CreativeTabs.tabRedstone);
        setUnlocalizedName(name);
    }
}
