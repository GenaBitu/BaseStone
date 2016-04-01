package cz.genabitu.basestone;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;

@Mod(modid=BaseStone.MODID, name=BaseStone.MODNAME, version=BaseStone.MODVER)
public class BaseStone {
    public static final String MODID = "basestone";
    public static final String MODNAME = "BaseStone";
    public static final String MODVER = "1.9";
    @Instance(value = BaseStone.MODID)
    public static BaseStone instance;

    public static Item baseStoneDust;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        baseStoneDust = new BaseStoneDust();
        GameRegistry.registerItem(baseStoneDust, "baseStoneDust");
    }
    @EventHandler
    public void load(FMLInitializationEvent event)
    {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(baseStoneDust, 0, new ModelResourceLocation("basestone:baseStoneDust", "inventory"));

    }
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    }
}
