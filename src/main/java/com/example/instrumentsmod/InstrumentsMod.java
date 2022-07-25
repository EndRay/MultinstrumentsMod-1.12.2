package com.example.instrumentsmod;

import com.example.instrumentsmod.creativetabs.MultinstrumentsTab;
import com.example.instrumentsmod.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = InstrumentsMod.MODID, name = InstrumentsMod.NAME, version = InstrumentsMod.VERSION)
public class InstrumentsMod {
    public static final String MODID = "instrumentsmod";
    public static final String NAME = "Example Mod";
    public static final String VERSION = "1.0";

    private static Logger logger;

    @SidedProxy(clientSide = "com.example.instrumentsmod.proxy.ClientProxy", serverSide = "com.example.instrumentsmod.proxy.CommonProxy")
    public static CommonProxy proxy;

    public static final CreativeTabs MULTINSTRUMENTS_TAB = new MultinstrumentsTab();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
        logger.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}