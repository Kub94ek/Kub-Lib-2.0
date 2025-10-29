package me.kub94ek.kubLib.recipes

import me.kub94ek.kubLib.KubLib
import me.kub94ek.kubLib.items.ItemRegistry
import org.bukkit.Bukkit
import org.bukkit.inventory.*
import org.bukkit.inventory.RecipeChoice.ExactChoice

/**
 * Manages the registration of custom recipes and tracks their usage.
 *
 * @param kubLib The main plugin instance.
 */
class RecipeRegistry(kubLib: KubLib) {

    /** The [ItemRegistry]. */
    private val itemRegistry = kubLib.itemRegistry

    /** Maps item IDs to the recipes they are used in. */
    val usedIn: HashMap<String, MutableList<String>> = hashMapOf()

    /**
     * Registers a recipe and tracks its usage.
     *
     * @param recipe The recipe to register.
     */
    fun registerRecipe(recipe: Recipe) {
        Bukkit.addRecipe(recipe)

        val result = recipe.result

        // Handle different recipe types and track their inputs.
        when (recipe) {
            is ShapedRecipe -> {
                addChoices(recipe.choiceMap.values, result)
            }
            is ShapelessRecipe -> {
                addChoices(recipe.choiceList, result)
            }
            is StonecuttingRecipe -> {
                addChoice(recipe.inputChoice, result)
            }
            is SmithingTransformRecipe -> {
                addChoice(recipe.base, result)
                addChoice(recipe.addition, result)
                addChoice(recipe.template, result)
            }
            is SmithingTrimRecipe -> {
                addChoice(recipe.base, result)
                addChoice(recipe.addition, result)
                addChoice(recipe.template, result)
            }
            is TransmuteRecipe -> {
                addChoice(recipe.input, result)
                addChoice(recipe.material, result)
            }
            else -> return
        }
    }

    /**
     * Adds multiple recipe choices to the usage map.
     *
     * @param choices The collection of recipe choices.
     * @param result The resulting item of the recipe.
     */
    private fun addChoices(choices: Collection<RecipeChoice>, result: ItemStack) {
        for (choice in choices) {
            addChoice(choice, result)
        }
    }

    /**
     * Adds a single recipe choice to the usage map.
     *
     * @param choice The recipe choice.
     * @param result The resulting item of the recipe.
     */
    private fun addChoice(choice: RecipeChoice, result: ItemStack) {
        if (choice !is ExactChoice) return

        choice.choices.forEach {
            addUsage(it, result)
        }
    }

    /**
     * Adds the usage of an item in a recipe.
     *
     * @param item The input item.
     * @param result The resulting item of the recipe.
     */
    private fun addUsage(item: ItemStack, result: ItemStack) {
        val itemId = itemRegistry.getItemId(item) ?: return
        val resultId = itemRegistry.getItemId(result) ?: "minecraft:${result.type.translationKey().lowercase()}"

        if (usedIn.containsKey(itemId)) {
            val usages = usedIn[itemId] ?: return
            if (usages.contains(resultId)) return
            usages.add(resultId)
            usedIn[itemId] = usages
        } else {
            usedIn[itemId] = mutableListOf(resultId)
        }
    }
}