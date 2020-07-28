package me.Fupery.ArtMap.Recipe;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface Ingredient {

    Material getMaterial();

    int getAmount();

    ItemStack toItemStack();

    class WrappedMaterial implements Ingredient {
        private final Material material;
        private final int amount;

		WrappedMaterial(Material material, int amount) {
            this.material = material;
            this.amount = amount;
        }

        @Override
        public Material getMaterial() {
            return material;
        }

        @Override
        public int getAmount() {
            return amount;
        }

        @Override
        public ItemStack toItemStack() {
			return new ItemStack(material, amount);
        }
    }

    class WrappedItem implements Ingredient {

        private final ItemStack item;

        public WrappedItem(ItemStack item) {
            this.item = item;
        }

        @Override
        public Material getMaterial() {
            return item.getType();
        }

        @Override
        public int getAmount() {
            return item.getAmount();
        }

        @Override
        public ItemStack toItemStack() {
            return item.clone();
        }
    }
}
