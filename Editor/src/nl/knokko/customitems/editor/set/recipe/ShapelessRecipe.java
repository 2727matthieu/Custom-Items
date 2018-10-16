package nl.knokko.customitems.editor.set.recipe;

import java.util.ArrayList;
import java.util.Collection;

import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.recipe.ingredient.Ingredient;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class ShapelessRecipe extends Recipe {
	
	private final Collection<Ingredient> ingredients;

	public ShapelessRecipe() {
		ingredients = new ArrayList<Ingredient>(9);
	}

	public ShapelessRecipe(BitInput input, ItemSet set) {
		super(input);
		byte ingredientCount = input.readByte();
		ingredients = new ArrayList<Ingredient>(ingredientCount);
		for (int counter = 0; counter < ingredientCount; counter++)
			ingredients.add(loadIngredient(input, set));
	}

	@Override
	protected void saveOwn(BitOutput output) {
		output.addByte((byte) ingredients.size());
		for (Ingredient ingredient : ingredients) {
			output.addByte(ingredient.getID());
			ingredient.save(output);
		}
	}
	
	public Collection<Ingredient> getIngredients(){
		return ingredients;
	}
}