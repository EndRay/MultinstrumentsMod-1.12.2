package com.example.instrumentsmod.creativetabs;

import com.example.instrumentsmod.InstrumentsMod;
import com.example.instrumentsmod.ItemsRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MultinstrumentsTab extends CreativeTabs {

    public MultinstrumentsTab() {
        super(InstrumentsMod.MODID);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(ItemsRegistry.MULTINSTRUMENT);
    }
}
