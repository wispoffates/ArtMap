package me.Fupery.ArtMap.Recipe;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Recipe;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.api.Config.Configuration;
import me.Fupery.ArtMap.IO.YamlReader;

public class RecipeLoader {
    private final FileConfiguration recipeFile;

    public RecipeLoader(ArtMap plugin, Configuration configuration) {
        YamlReader reader = new YamlReader(plugin, "recipe.yml");
        recipeFile = configuration.CUSTOM_RECIPES ? reader.tryDataFolder() : reader.readFromResources();
    }

    public void loadRecipes() {
        ArtMaterial.setupRecipes();
    }

    public void unloadRecipes() {
        Iterator<Recipe> i = Bukkit.getServer().recipeIterator();
        while (i.hasNext()) {
            if (ArtMaterial.getCraftItemType(i.next().getResult()) != null) i.remove();
        }
    }

    SimpleRecipe getRecipe(String recipeName) throws InvalidRecipeException {
        ConfigurationSection recipeData = recipeFile.getConfigurationSection(recipeName);
        if (recipeData == null) return null;
        ConfigurationSection recipeMaterials = recipeData.getConfigurationSection("MATERIALS");

        if (recipeMaterials == null || recipeMaterials.getKeys(false).size() < 2)
            throw new InvalidRecipeException(recipeName, "Recipe cannot have less than two materials");

        List<String> shape = recipeData.getStringList("SHAPE");
        boolean recipeIsShaped = !shape.isEmpty();
        HashMap<Character, Ingredient> materials = readRecipeMaterials(recipeName, recipeMaterials);

		SimpleRecipe recipe = recipeIsShaped ? new SimpleRecipe.Shaped(recipeName)
				: new SimpleRecipe.Shapeless(recipeName);

        if (recipeIsShaped) {
            validateRecipeShape(recipeName, shape);
            ((SimpleRecipe.Shaped) recipe).shape(shape.get(0), shape.get(1), shape.get(2));
        }

        for (Entry<Character,Ingredient> ent : materials.entrySet()) {
            Ingredient material = ent.getValue();
            if (recipeIsShaped) {
                ((SimpleRecipe.Shaped) recipe).set(ent.getKey(), material);
            } else {
                ((SimpleRecipe.Shapeless) recipe).add(material);
            }
        }
        return recipe;
    }

    private void validateRecipeShape(String recipeName, List<String> shape) throws InvalidRecipeException {
        if (shape.size() != 3) throw new InvalidRecipeException(recipeName, "Recipe shape must have 3 lines");
        for (String line : shape) {
            if (line.length() != 3) {
                throw new InvalidRecipeException(recipeName, "Recipe shape must have 3 characters per line.");
            }
        }
    }

    private HashMap<Character, Ingredient> readRecipeMaterials(String recipeName, ConfigurationSection materialList)
            throws InvalidRecipeException {
        HashMap<Character, Ingredient> materials = new HashMap<>();
        for (String key : materialList.getKeys(false)) {
            if (key.length() > 1)
                throw new InvalidMaterialKeyException(recipeName, key, "is not a valid material key");
            if (materials.containsKey(key.charAt(0)))
                throw new InvalidMaterialKeyException(recipeName, key, "cannot be used as a key more than once");

            String materialName = materialList.isConfigurationSection(key) ?
                    materialList.getString(key + ".MATERIAL") :
                    materialList.getString(key);
            Material material;
            int amount = 1;
            try {
                material = Material.valueOf(materialName);
            } catch (IllegalArgumentException e) {
                throw new InvalidMaterialKeyException(recipeName, materialName, "is not a valid material");
            }
			// if (materialList.contains(key + ".DURABILITY")) durability =
			// materialList.getInt(key + ".DURABILITY");
            if (materialList.contains(key + ".AMOUNT")) amount = materialList.getInt(key + ".AMOUNT");
			materials.put(key.charAt(0), new Ingredient.WrappedMaterial(material, amount));
        }
        return materials;
    }

    static class InvalidRecipeException extends Exception {
		/**  */
		private static final long serialVersionUID = 1L;

		private InvalidRecipeException(String recipeName, String message) {
            super("Error loading recipe for " + recipeName + ": " + message);
        }
    }

    private static class InvalidMaterialKeyException extends InvalidRecipeException {
		/**  */
		private static final long serialVersionUID = 1L;

		private InvalidMaterialKeyException(String recipeName, String key, String message) {
            super(recipeName, String.format("'%s' %s", key, message));
        }
    }
}
