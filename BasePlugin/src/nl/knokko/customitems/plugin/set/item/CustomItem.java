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

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;

import nl.knokko.core.plugin.item.attributes.ItemAttributes;
import nl.knokko.core.plugin.item.attributes.ItemAttributes.Single;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.Enchantment;

public abstract class CustomItem extends nl.knokko.customitems.item.CustomItem {
	
	public static boolean isCustom(ItemStack item) {
		return item != null && item.hasItemMeta() && item.getItemMeta().isUnbreakable() && item.getDurability() > 0;
	}
	
	protected final Material material;
	
	protected final Single[] attributeModifiers;
    
    public CustomItem(CustomItemType itemType, short itemDamage, String name, String displayName, 
    		String[] lore, AttributeModifier[] attributes, Enchantment[] defaultEnchantments, boolean[] itemFlags){
        super(itemType, itemDamage, name, displayName, lore, attributes, defaultEnchantments, itemFlags);
        
        // Why Bukkit?
        material = Material.getMaterial(itemType.name().replace("SHOVEL", "SPADE"));
        attributeModifiers = new Single[attributes.length];
        for (int index = 0; index < attributes.length; index++) {
        	AttributeModifier a = attributes[index];
        	attributeModifiers[index] = new Single(a.getAttribute().getName(), a.getSlot().getSlot(), a.getOperation().getOperation(), a.getValue());
        }
    }
    
    protected List<String> createLore(){
    	return Lists.newArrayList(lore);
    }
    
    protected ItemMeta createItemMeta(ItemStack item, List<String> lore) {
    	ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(lore);
        meta.setUnbreakable(true);
        ItemFlag[] allFlags = ItemFlag.values();
        for (int index = 0; index < allFlags.length; index++) {
        	if (itemFlags[index]) {
        		meta.addItemFlags(allFlags[index]);
        	}
        }
        return meta;
    }
    
    public ItemStack create(int amount, List<String> lore){
    	ItemStack item = ItemAttributes.createWithAttributes(material, amount, attributeModifiers);
        item.setItemMeta(createItemMeta(item, lore));
        item.setDurability(itemDamage);
        for (Enchantment enchantment : defaultEnchantments) {
        	item.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.getByName(enchantment.getType().name()), enchantment.getLevel());
        }
        return item;
    }
    
    public ItemStack create(int amount) {
    	return create(amount, createLore());
    }
    
    public static short getDamage(ItemStack item) {
    	return item.getDurability();
    }
    
    public boolean forbidDefaultUse(ItemStack item) {
    	return true;
    }
    
    public boolean is(ItemStack item){
    	/*
    	 * This is my debug for when custom shit doesn't work
    	if (item.getType() == material)
    		System.out.println("CustomItem.is: other durability is " + item.getDurability() + " and unbreakable is " + item.getItemMeta().isUnbreakable());
    	else
    		System.out.println("CustomItem.is: wrong material");
    		*/
        return item != null && item.hasItemMeta() && item.getItemMeta().isUnbreakable() && item.getType() == material && getDamage(item) == itemDamage;
    }
    
    public Material getMaterial() {
    	return material;
    }
    
    public boolean canStack() {
    	return getMaxStacksize() > 1;
    }
    
    public abstract int getMaxStacksize();
    
    public boolean allowVanillaEnchanting() {
    	return false;
    }
    
    public boolean allowAnvilActions() {
    	return false;
    }
    
    public void onBlockBreak(Player player, ItemStack item, Block block) {}
    
    public void onEntityHit(LivingEntity attacker, ItemStack weapon, Entity target) {}
}