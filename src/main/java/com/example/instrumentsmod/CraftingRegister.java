package com.example.instrumentsmod;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;

public class CraftingRegister {
    public static void register() {
        registerRecipes("coagulatorrecipe");
    }

    private static void registerRecipes(String name) {
        CraftingHelper.register(new ResourceLocation("instrumentsmod", name), (IRecipeFactory) (context, json) -> CraftingHelper.getRecipe(json, context));
    }
}