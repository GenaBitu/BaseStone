package cz.genabitu.basestone;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

@Mod(modid=BaseStone.MODID, name=BaseStone.MODNAME, version=BaseStone.MODVER)
public class BaseStone {
    public static final String MODID = "basestone";
    public static final String MODNAME = "BaseStone";
    public static final String MODVER = "1.9";
    @Instance(value = BaseStone.MODID)
    public static BaseStone instance;
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    }
    @EventHandler
    public void load(FMLInitializationEvent event)
    {
    }
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    }
}
