package cz.genabitu.basestone;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class BaseStoneDust extends Item {
    public BaseStoneDust()
    {
        setMaxStackSize(64);
        setCreativeTab(CreativeTabs.tabRedstone);
        setUnlocalizedName("baseStoneDust");
    }
}
