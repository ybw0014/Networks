package com.ytdd9527.networks.expansion.core.item.machine.autocrafter.basic;

import com.ytdd9527.networks.expansion.core.item.machine.autocrafter.systems.blueprint.MagicWorkbenchBlueprint;
import com.ytdd9527.networks.expansion.core.item.machine.autocrafter.systems.supportedrecipes.SupportedMagicWorkbenchRecipes;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Set;

public class AutoMagicWorkbenchCrafter extends AbstractAutoCrafter {
    public AutoMagicWorkbenchCrafter(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            int chargePerCraft,
            boolean withholding
    ) {
        super(itemGroup, item, recipeType, recipe, chargePerCraft, withholding);
    }

    public Set<Map.Entry<ItemStack[], ItemStack>> getRecipeEntries() {
        return SupportedMagicWorkbenchRecipes.getRecipes().entrySet();
    }

    public boolean getRecipeTester(ItemStack[] inputs, ItemStack[] recipe) {
        return SupportedMagicWorkbenchRecipes.testRecipe(inputs, recipe);
    }

    public boolean isValidBlueprint(SlimefunItem item) {
        return item instanceof MagicWorkbenchBlueprint;
    }
}