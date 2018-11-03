package nl.knokko.customitems.plugin.set.item;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;

import nl.knokko.core.plugin.item.attributes.ItemAttributes;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;

public class CustomTool extends CustomItem {
	
	private static final String DURABILITY_PREFIX = "Durability: ";
	private static final String DURABILITY_SPLIT = " / ";
	
	private final int maxDurability;
	
	private final boolean allowEnchanting;
	private final boolean allowAnvil;
	
	private final boolean isSword;

	public CustomTool(CustomItemType itemType, short itemDamage, String name, String displayName, String[] lore, 
			AttributeModifier[] attributes, int maxDurability, boolean allowEnchanting, boolean allowAnvil) {
		super(itemType, itemDamage, name, displayName, lore, attributes);
		this.maxDurability = maxDurability;
		this.allowEnchanting = allowEnchanting;
		this.allowAnvil = allowAnvil;
		isSword = itemType == CustomItemType.WOOD_SWORD || itemType == CustomItemType.STONE_SWORD
				|| itemType == CustomItemType.IRON_SWORD || itemType == CustomItemType.GOLD_SWORD
				|| itemType == CustomItemType.DIAMOND_SWORD;
	}
	
	@Override
	public boolean canStack() {
		return false;
	}
	
	@Override
	public boolean allowVanillaEnchanting() {
		return allowEnchanting;
	}
	
	@Override
	public boolean allowAnvilActions() {
		return allowAnvil;
	}
	
	@Override
	public ItemStack create(int amount) {
		if (amount != 1) throw new IllegalArgumentException("Amount must be 1, but is " + amount);
		ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(material);
        meta.setDisplayName(displayName);
        if (maxDurability == nl.knokko.customitems.item.CustomItem.UNBREAKABLE_TOOL_DURABILITY) {
        	meta.setLore(Lists.newArrayList(lore));
        } else {
        	List<String> itemLore = new ArrayList<String>(lore.length + 2);
        	itemLore.add(DURABILITY_PREFIX + maxDurability + DURABILITY_SPLIT + maxDurability);
        	itemLore.add("");
        	for (String s : lore)
        		itemLore.add(s);
        	meta.setLore(itemLore);
        }
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(meta);
        item.setDurability(itemDamage);
        return ItemAttributes.clearAttributes(item);
	}
	
	@Override
	public void onBlockBreak(Player player, ItemStack tool, Block block) {
		if (decreaseDurability(tool, isSword ? 2 : 1))
			player.getInventory().setItemInMainHand(null);
	}
	
	@Override
	public void onEntityHit(LivingEntity attacker, ItemStack tool, Entity target) {
		if (decreaseDurability(tool, isSword ? 1 : 2))
			attacker.getEquipment().setItemInMainHand(null);
	}
	
	public boolean forbidDefaultUse(ItemStack item) {
    	return false;
    }
	
	/**
	 * @param stack The (custom) item stack to decrease the durability of
	 * @return True if the stack breaks, false if it only loses durability
	 */
	public boolean decreaseDurability(ItemStack stack, int damage) {
		ItemMeta meta = stack.getItemMeta();
		List<String> lore = meta.getLore();
		String durabilityString = lore.get(0);
		// Check whether or not the tool is unbreakable
		if (durabilityString.startsWith(DURABILITY_PREFIX)) {
			int durability = Integer.parseInt(durabilityString.substring(DURABILITY_PREFIX.length(), durabilityString.indexOf(DURABILITY_SPLIT)));
			if (durability > 1) {
				durability -= damage;
				lore.set(0, DURABILITY_PREFIX + durability + DURABILITY_SPLIT + maxDurability);
				meta.setLore(lore);
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}
}