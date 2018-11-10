/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2018 knokko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *  
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package nl.knokko.customitems.editor.menu.edit.recipe;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.recipe.ingredient.IngredientComponent;
import nl.knokko.customitems.editor.menu.edit.recipe.result.ResultComponent;
import nl.knokko.customitems.editor.set.item.Material;
import nl.knokko.customitems.editor.set.recipe.ShapedRecipe;
import nl.knokko.customitems.editor.set.recipe.ingredient.Ingredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.NoIngredient;
import nl.knokko.customitems.editor.set.recipe.result.SimpleVanillaResult;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.color.SimpleGuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.component.text.TextComponent;

public class ShapedRecipeEdit extends GuiMenu {
	
	private static final GuiColor INGREDIENT_BACKGROUND = new SimpleGuiColor(200, 100, 0);
	
	private final EditMenu menu;
	private final ShapedRecipe previous;
	private final Ingredients ingredientsComponent;
	private final ResultComponent resultComponent;
	private final TextComponent errorComponent;

	public ShapedRecipeEdit(EditMenu menu, ShapedRecipe previous) {
		this.menu = menu;
		this.previous = previous;
		errorComponent = new TextComponent("", EditProps.ERROR);
		if (previous != null)
			resultComponent = new ResultComponent(previous.getResult(), this, menu.getSet());
		else
			resultComponent = new ResultComponent(new SimpleVanillaResult(Material.IRON_INGOT, (byte) 1), this, menu.getSet());
		if (previous != null)
			ingredientsComponent = new Ingredients(previous.getIngredients());
		else
			ingredientsComponent = new Ingredients();
	}

	@Override
	protected void addComponents() {
		addComponent(new TextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(menu.getRecipeOverview());
		}), 0.1f, 0.85f, 0.25f, 0.95f);
		addComponent(new TextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			if (previous != null) {
				Ingredient[] ingredients = new Ingredient[9];
				for (int index = 0; index < ingredients.length; index++)
					ingredients[index] = ingredientsComponent.ingredients[index].getIngredient();
				String error = menu.getSet().changeShapedRecipe(previous, ingredients, resultComponent.getResult());
				if (error != null)
					errorComponent.setText(error);
				else
					state.getWindow().setMainComponent(menu.getRecipeOverview());
			} else {
				Ingredient[] ingredients = new Ingredient[9];
				for (int index = 0; index < ingredients.length; index++)
					ingredients[index] = ingredientsComponent.ingredients[index].getIngredient();
				String error = menu.getSet().addShapedRecipe(ingredients, resultComponent.getResult());
				if (error != null)
					errorComponent.setText(error);
				else
					state.getWindow().setMainComponent(menu.getRecipeOverview());
			}
		}), 0.1f, 0.7f, 0.25f, 0.8f);
		addComponent(ingredientsComponent, 0.05f, 0.1f, 0.65f, 0.6f);
		addComponent(errorComponent, 0.35f, 0.85f, 0.95f, 0.95f);
		addComponent(resultComponent, 0.75f, 0.275f, 0.95f, 0.425f);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	private class Ingredients extends GuiMenu {
		
		private final IngredientComponent[] ingredients;
		
		private Ingredients() {
			ingredients = new IngredientComponent[9];
			for (int index = 0; index < ingredients.length; index++)
				ingredients[index] = new IngredientComponent(new NoIngredient(), ShapedRecipeEdit.this, menu.getSet());
		}
		
		private Ingredients(Ingredient[] ingredients) {
			this.ingredients = new IngredientComponent[9];
			for (int index = 0; index < ingredients.length; index++)
				this.ingredients[index] = new IngredientComponent(ingredients[index], ShapedRecipeEdit.this, menu.getSet());
		}

		@Override
		protected void addComponents() {
			addComponent(ingredients[0], 0.025f, 0.675f, 0.325f, 0.975f);
			addComponent(ingredients[1], 0.350f, 0.675f, 0.650f, 0.975f);
			addComponent(ingredients[2], 0.675f, 0.675f, 0.975f, 0.975f);
			addComponent(ingredients[3], 0.025f, 0.350f, 0.325f, 0.650f);
			addComponent(ingredients[4], 0.350f, 0.350f, 0.650f, 0.650f);
			addComponent(ingredients[5], 0.675f, 0.350f, 0.975f, 0.650f);
			addComponent(ingredients[6], 0.025f, 0.025f, 0.325f, 0.325f);
			addComponent(ingredients[7], 0.350f, 0.025f, 0.650f, 0.325f);
			addComponent(ingredients[8], 0.675f, 0.025f, 0.975f, 0.325f);
		}
		
		@Override
		public GuiColor getBackgroundColor() {
			return INGREDIENT_BACKGROUND;
		}
	}
}