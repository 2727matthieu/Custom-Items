/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2019 knokko
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
package nl.knokko.customitems.plugin.set.item;

import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.customitems.plugin.recipe.ingredient.Ingredient;

public class CustomArmor extends CustomTool {
	
	private final Color color;

	public CustomArmor(CustomItemType itemType, short itemDamage, String name, String displayName, String[] lore,
			AttributeModifier[] attributes, Enchantment[] defaultEnchantments, long maxDurability,
			boolean allowEnchanting, boolean allowAnvil, Ingredient repairItem, Color color, boolean[] itemFlags,
			int entityHitDurabilityLoss, int blockBreakDurabilityLoss) {
		super(itemType, itemDamage, name, displayName, lore, attributes, defaultEnchantments, maxDurability,
				allowEnchanting, allowAnvil, repairItem, itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss);
		this.color = color;
	}
	
	@Override
	public ItemStack create(int amount, long durability) {
		ItemStack base = super.create(amount, durability);
		CustomItemType i = itemType;
		if (i == CustomItemType.LEATHER_HELMET || i == CustomItemType.LEATHER_CHESTPLATE
				|| i == CustomItemType.LEATHER_LEGGINGS || i == CustomItemType.LEATHER_BOOTS) {
			LeatherArmorMeta meta = (LeatherArmorMeta) base.getItemMeta();
			meta.setColor(color);
			base.setItemMeta(meta);
		}
		return base;
	}
}