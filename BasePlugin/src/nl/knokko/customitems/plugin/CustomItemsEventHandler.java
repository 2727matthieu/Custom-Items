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
package nl.knokko.customitems.plugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

import nl.knokko.customitems.plugin.recipe.CustomRecipe;
import nl.knokko.customitems.plugin.recipe.ShapedCustomRecipe;
import nl.knokko.customitems.plugin.recipe.ShapelessCustomRecipe;
import nl.knokko.customitems.plugin.set.ItemSet;
import nl.knokko.customitems.plugin.set.item.CustomItem;
import nl.knokko.customitems.plugin.set.item.CustomTool;

import static org.bukkit.enchantments.Enchantment.*;

public class CustomItemsEventHandler implements Listener {

	private Map<UUID, Boolean> shouldInterfere = new HashMap<UUID, Boolean>();

	@EventHandler
	public void onItemUse(PlayerInteractEvent event) {
		ItemStack item = event.getItem();
		CustomItem custom = CustomItemsPlugin.getInstance().getSet().getItem(item);
		if (custom != null && custom.forbidDefaultUse(item)) {
			// Don't let custom items be used as their internal item
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onShear(PlayerShearEntityEvent event) {
		ItemStack main = event.getPlayer().getInventory().getItemInMainHand();
		ItemStack off = event.getPlayer().getInventory().getItemInOffHand();
		CustomItem customMain = main.getType() == Material.SHEARS
				? CustomItemsPlugin.getInstance().getSet().getItem(main)
				: null;
		CustomItem customOff = off.getType() == Material.SHEARS ? CustomItemsPlugin.getInstance().getSet().getItem(off)
				: null;
		if ((customMain != null && customMain.forbidDefaultUse(main))
				|| (customOff != null && customOff.forbidDefaultUse(off))) {
			// Don't let custom shears be used as real shears
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
		if (CustomItem.isCustom(item)) {
			CustomItem custom = CustomItemsPlugin.getInstance().getSet().getItem(item);
			if (custom != null) {
				custom.onBlockBreak(event.getPlayer(), item, event.getBlock());
			} else {
				Bukkit.getLogger().warning("Interesting item: " + item);
			}
		}
	}

	@EventHandler
	public void onEntityHit(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof LivingEntity) {
			ItemStack weapon = ((LivingEntity) event.getDamager()).getEquipment().getItemInMainHand();
			if (CustomItem.isCustom(weapon)) {
				CustomItem custom = CustomItemsPlugin.getInstance().getSet().getItem(weapon);
				if (custom != null) {
					custom.onEntityHit((LivingEntity) event.getDamager(), weapon, event.getEntity());
				} else {
					Bukkit.getLogger().warning("Interesting item: " + weapon);
				}
			}
		}
	}

	@EventHandler
	public void onItemInteract(PlayerInteractAtEntityEvent event) {
		ItemStack item;
		if (event.getHand() == EquipmentSlot.HAND)
			item = event.getPlayer().getInventory().getItemInMainHand();
		else
			item = event.getPlayer().getInventory().getItemInOffHand();
		CustomItem custom = CustomItemsPlugin.getInstance().getSet().getItem(item);
		if (custom != null && custom.forbidDefaultUse(item)) {
			// Don't let custom items be used as their internal item
			event.setCancelled(true);
		}
	}

	/*
	 * The mending enchantment is ignored on unbreakable items
	 * 
	 * @EventHandler(priority = EventPriority.HIGHEST) public void
	 * handleCustomMending(PlayerItemMendEvent event) { if
	 * (CustomItem.isCustom(event.getItem())) { CustomItem custom =
	 * CustomItemsPlugin.getInstance().getSet().getItem(event.getItem()); if (custom
	 * != null) { if (custom instanceof CustomTool) { CustomTool tool = (CustomTool)
	 * custom; int repaired = tool.increaseDurability(event.getItem(),
	 * event.getRepairAmount()); int newXP =
	 * event.getExperienceOrb().getExperience() - repaired / 2; if (newXP < 0)
	 * event.getExperienceOrb().remove();
	 * event.getExperienceOrb().setExperience(newXP); } event.setCancelled(true); }
	 * } }
	 */

	@EventHandler
	public void beforeXP(PlayerExpChangeEvent event) {
		ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
		if (CustomItem.isCustom(item)) {
			CustomItem custom = CustomItemsPlugin.getInstance().getSet().getItem(item);
			if (custom != null) {
				if (custom instanceof CustomTool) {
					CustomTool tool = (CustomTool) custom;
					int repaired = tool.increaseDurability(item, event.getAmount() * 2);
					int newXP = event.getAmount() - repaired / 2;
					event.setAmount(newXP);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void processAnvil(PrepareAnvilEvent event) {
		ItemStack[] contents = event.getInventory().getStorageContents();
		CustomItem custom1 = null;
		CustomItem custom2 = null;
		if (CustomItem.isCustom(contents[0]))
			custom1 = CustomItemsPlugin.getInstance().getSet().getItem(contents[0]);
		if (CustomItem.isCustom(contents[1]))
			custom2 = CustomItemsPlugin.getInstance().getSet().getItem(contents[1]);
		if (custom1 != null) {
			if (custom1.allowAnvilActions()) {
				if (custom1 instanceof CustomTool) {
					if (custom1 == custom2) {
						CustomTool tool = (CustomTool) custom1;
						int durability1 = tool.getDurability(contents[0]);
						int durability2 = tool.getDurability(contents[1]);
						int resultDurability = Math.min(durability1 + durability2, tool.getMaxDurability());
						Map<Enchantment, Integer> enchantments1 = contents[0].getEnchantments();
						Map<Enchantment, Integer> enchantments2 = contents[1].getEnchantments();
						ItemStack result = tool.create(1, resultDurability);
						int levelCost = 0;
						boolean hasChange = false;
						if (!event.getInventory().getRenameText().isEmpty() && !event.getInventory().getRenameText().equals(tool.getDisplayName())) {
							ItemMeta meta = result.getItemMeta();
							meta.setDisplayName(event.getInventory().getRenameText());
							result.setItemMeta(meta);
							levelCost++;
							hasChange = true;
						}
						result.addUnsafeEnchantments(enchantments1);
						Set<Entry<Enchantment, Integer>> entrySet = enchantments2.entrySet();
						for (Entry<Enchantment, Integer> entry : entrySet) {
							if (entry.getKey().canEnchantItem(result)) {
								try {
									result.addEnchantment(entry.getKey(), entry.getValue());
									levelCost += entry.getValue() * getItemEnchantFactor(entry.getKey());
									hasChange = true;
								} catch (IllegalArgumentException illegal) {
									// The rules from the wiki
									levelCost++;
								} // Only add enchantments that can be added
							}
						}
						int repairCost1 = 0;
						int repairCost2 = 0;
						ItemMeta meta1 = contents[0].getItemMeta();
						if (meta1 instanceof Repairable) {
							Repairable repairable = (Repairable) meta1;
							repairCost1 = repairable.getRepairCost();
							levelCost += repairCost1;
						}
						ItemMeta meta2 = contents[1].getItemMeta();
						if (meta2 instanceof Repairable) {
							Repairable repairable = (Repairable) meta2;
							repairCost2 = repairable.getRepairCost();
							levelCost += repairCost2;
						}
						ItemMeta resultMeta = result.getItemMeta();
						int maxRepairCost = Math.max(repairCost1, repairCost2);
						int maxRepairCount = (int) Math.round(Math.log(maxRepairCost + 1) / Math.log(2));
						((Repairable) resultMeta).setRepairCost((int) Math.round(Math.pow(2, maxRepairCount + 1) - 1));
						result.setItemMeta(resultMeta);
						if (tool.getDurability(contents[0]) < tool.getMaxDurability()) {
							levelCost += 2;
							hasChange = true;
						}
						if (hasChange) {
							event.setResult(result);
							if (levelCost < 2)
								levelCost = 2;
							int finalLevelCost = levelCost;
							Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
								// Apparently, settings the repair cost during the event has no effect
								event.getInventory().setRepairCost(finalLevelCost);
							});
						} else {
							event.setResult(null);
						}
					} else if (contents[1] != null && contents[1].getType() == Material.ENCHANTED_BOOK) {
						/*
						 * Ehm... yes... I kinda forgot this works fine automatically before writing this...
						 * 
						ItemMeta meta2 = contents[1].getItemMeta();
						ItemStack result = contents[0].clone();
						if (meta2 instanceof EnchantmentStorageMeta) {
							EnchantmentStorageMeta esm = (EnchantmentStorageMeta) meta2;
							int levelCost = 2;
							Set<Entry<Enchantment,Integer>> entrySet = esm.getStoredEnchants().entrySet();
							for (Entry<Enchantment,Integer> entry : entrySet) {
								if (entry.getKey().canEnchantItem(result)) {
									try {
										result.addEnchantment(entry.getKey(), entry.getValue());
										levelCost += entry.getValue() * getBookEnchantFactor(entry.getKey());
									} catch (IllegalArgumentException illegal) {
										// The rules from the wiki
										levelCost++;
									} // Only add enchantments that can be added
								}
							}
							int repairCount1 = 0;
							int repairCount2 = 0;
							ItemMeta meta1 = contents[0].getItemMeta();
							if (meta1 instanceof Repairable) {
								Repairable repairable = (Repairable) meta1;
								System.out.println("repairable1: " + repairable.getRepairCost());
								repairCount1 = repairable.getRepairCost();
								levelCost += Math.pow(2, repairCount1) - 1;
							}
							if (meta2 instanceof Repairable) {
								Repairable repairable = (Repairable) meta2;
								System.out.println("repairable2: " + repairable.getRepairCost());
								repairCount2 = repairable.getRepairCost();
								levelCost += Math.pow(2, repairCount2) - 1;
							}
							if (!event.getInventory().getRenameText().isEmpty()) {
								ItemMeta meta = result.getItemMeta();
								meta.setDisplayName(event.getInventory().getRenameText());
								result.setItemMeta(meta);
								levelCost++;
							}
							ItemMeta resultMeta = result.getItemMeta();
							((Repairable) resultMeta).setRepairCost(Math.max(repairCount1, repairCount2) + 1);
							result.setItemMeta(resultMeta);
							event.setResult(result);
							int finalLevelCost = levelCost;
							Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
								// Apparently, settings the repair cost during the event has no effect
								event.getInventory().setRepairCost(finalLevelCost);
							});
						} else {
							event.setResult(null);
						}*/
					}
				} else {
					event.setResult(null);
				}
			} else if (contents[1] == null || contents[1].getType() == Material.AIR) {
				// Don't prevent renaming
			} else {
				event.setResult(null);
			}
		} else if (custom2 != null) {
			event.setResult(null);
		}
	}

	private static int getItemEnchantFactor(Enchantment e) {
		if (e.equals(PROTECTION_FIRE) || e.equals(PROTECTION_FALL)
				|| e.equals(PROTECTION_PROJECTILE) || e.equals(DAMAGE_UNDEAD)
				|| e.equals(DAMAGE_ARTHROPODS) || e.equals(KNOCKBACK) || e.equals(DURABILITY)) {
			return 2;
		}
		if (e.equals(PROTECTION_EXPLOSIONS) || e.equals(OXYGEN) || e.equals(WATER_WORKER)
				|| e.equals(DEPTH_STRIDER) || e.equals(FROST_WALKER) || e.equals(FIRE_ASPECT)
				|| e.equals(LOOT_BONUS_MOBS) || e.equals(SWEEPING_EDGE)
				|| e.equals(LOOT_BONUS_BLOCKS) || e.equals(ARROW_KNOCKBACK) || e.equals(ARROW_FIRE)
				|| e.equals(LUCK) || e.equals(LURE) || e.equals(MENDING)) {
			return 4;
		}
		if (e.equals(THORNS) || e.equals(BINDING_CURSE) || e.equals(SILK_TOUCH)
				|| e.equals(ARROW_INFINITE) || e.equals(VANISHING_CURSE)) {
			return 8;
		}
		return 1;
	}
	
	/*
	private static int getBookEnchantFactor(Enchantment e) {
		if (e == Enchantment.PROTECTION_EXPLOSIONS || e == Enchantment.OXYGEN || e == Enchantment.WATER_WORKER
				|| e == Enchantment.DEPTH_STRIDER || e == Enchantment.FROST_WALKER || e == Enchantment.LOOT_BONUS_MOBS
				|| e == Enchantment.SWEEPING_EDGE || e == Enchantment.LOOT_BONUS_BLOCKS
				|| e == Enchantment.ARROW_KNOCKBACK || e == Enchantment.ARROW_FIRE || e == Enchantment.LUCK
				|| e == Enchantment.LURE || e == Enchantment.MENDING) {
			return 2;
		}
		if (e == Enchantment.THORNS || e == Enchantment.BINDING_CURSE || e == Enchantment.SILK_TOUCH
				|| e == Enchantment.ARROW_INFINITE || e == Enchantment.VANISHING_CURSE) {
			return 4;
		}
		return 1;
	}*/

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void cancelEnchanting(PrepareItemEnchantEvent event) {
		CustomItem custom = CustomItemsPlugin.getInstance().getSet().getItem(event.getItem());
		if (custom != null && !custom.allowVanillaEnchanting())
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		SlotType type = event.getSlotType();
		InventoryAction action = event.getAction();
		if (type == SlotType.RESULT) {
			if (event.getInventory() instanceof CraftingInventory) {
				if (shouldInterfere.getOrDefault(event.getWhoClicked().getUniqueId(), false)) {
					if (action == InventoryAction.PICKUP_ALL) {
						// This block deals with normal crafting
						Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
							/*
							 * For every itemstack in crafting matrix when 1 item was crafted: actualAmount
							 * = 2 * (initialAmount - 1) desiredAmount = initialAmount - 1 desiredAmount =
							 * actualAmount / 2;
							 */
							ItemStack[] contents = event.getInventory().getContents();
							for (int index = 1; index < contents.length; index++) {
								contents[index].setAmount(contents[index].getAmount() / 2);
							}
						});
					} else if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
						// This block deals with shift clicks on the result slot
						int amountPerCraft = event.getCurrentItem().getAmount();
						int minAmount = 64;
						ItemStack[] contents = event.getInventory().getContents();
						ItemStack result = contents[0].clone();
						for (int index = 1; index < contents.length; index++)
							if (contents[index].getType() != Material.AIR && contents[index].getAmount() < minAmount)
								minAmount = contents[index].getAmount();
						event.setResult(Result.DENY);
						for (int index = 1; index < contents.length; index++)
							contents[index].setAmount(contents[index].getAmount() - minAmount);
						event.getInventory().setItem(0, new ItemStack(Material.AIR));
						CustomItem customResult = CustomItemsPlugin.getInstance().getSet().getItem(result);
						int amountToGive = amountPerCraft * minAmount;
						if (customResult != null && !customResult.canStack()) {
							for (int counter = 0; counter < amountToGive; counter++) {
								event.getWhoClicked().getInventory().addItem(result);
							}
						} else {
							for (int counter = 0; counter < amountToGive; counter += 64) {
								int left = amountToGive - counter;
								if (left > 64) {
									result.setAmount(64);
									event.getWhoClicked().getInventory().addItem(result);
								} else {
									result.setAmount(left);
									event.getWhoClicked().getInventory().addItem(result);
									break;
								}
							}
						}
					} else if (action == InventoryAction.NOTHING) {
						// This case is possible when a custom item is on the cursor because it isn't
						// really stackable
						ItemStack cursor = event.getCursor();
						ItemStack current = event.getCurrentItem();
						if (CustomItem.isCustom(cursor) && CustomItem.isCustom(current)
								&& cursor.getType() == current.getType()
								&& cursor.getDurability() == current.getDurability()) {
							CustomItem custom = CustomItemsPlugin.getInstance().getSet().getItem(current);
							if (custom != null && custom.canStack()) {
								Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
									ItemStack[] contents = event.getInventory().getContents();
									for (int index = 1; index < contents.length; index++) {
										if (contents[index].getAmount() > 1)
											contents[index].setAmount(contents[index].getAmount() - 1);
										else
											contents[index] = null;
									}
									event.getInventory().setContents(contents);
									cursor.setAmount(cursor.getAmount() + current.getAmount());
									event.getView().getPlayer().setItemOnCursor(cursor);
									beforeCraft((CraftingInventory) event.getInventory(), event.getView().getPlayer());
								});
							}
						}
					} else {
						// Maybe, there is some edge case I don't know about, so cancel it just to be
						// sure
						event.setResult(Result.DENY);
					}
				}
			} else if (event.getInventory() instanceof AnvilInventory) {
				// By default, Minecraft does not allow players to pick illegal items from
				// anvil, so...
				ItemStack cursor = event.getCursor();
				ItemStack current = event.getCurrentItem();
				if ((cursor == null || cursor.getType() == Material.AIR) && CustomItem.isCustom(current)) {
					AnvilInventory ai = (AnvilInventory) event.getInventory();
					if (event.getView().getPlayer() instanceof Player) {
						Player player = (Player) event.getView().getPlayer();
						int repairCost = ai.getRepairCost();
						if (player.getLevel() >= repairCost) {
							player.setItemOnCursor(current);
							player.setLevel(player.getLevel() - repairCost);
							ai.setContents(new ItemStack[3]);
						}
					}
				}
			}
		} else if (action == InventoryAction.NOTHING || action == InventoryAction.PICKUP_ONE
				|| action == InventoryAction.PICKUP_SOME || action == InventoryAction.SWAP_WITH_CURSOR) {
			ItemStack cursor = event.getCursor();
			ItemStack current = event.getCurrentItem();
			// This block makes custom items stackable
			if (CustomItem.isCustom(cursor) && CustomItem.isCustom(current)) {
				ItemSet set = CustomItemsPlugin.getInstance().getSet();
				CustomItem customCursor = set.getItem(cursor);
				CustomItem customCurrent = set.getItem(current);
				if (customCursor != null && customCursor == customCurrent && customCursor.canStack()) {
					event.setResult(Result.DENY);
					int amount = current.getAmount() + cursor.getAmount();
					if (amount <= 64) {
						current.setAmount(amount);
						Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
							event.getView().getPlayer().setItemOnCursor(new ItemStack(Material.AIR));
						});
					} else {
						current.setAmount(64);
						cursor.setAmount(amount - 64);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void beforeCraft(PrepareItemCraftEvent event) {
		beforeCraft(event.getInventory(), event.getView().getPlayer());
	}

	public void beforeCraft(CraftingInventory inventory, HumanEntity owner) {
		ItemStack result = inventory.getResult();

		// Block vanilla recipes that attempt to use custom items
		if (result != null && result.getType() != Material.AIR) {
			ItemStack[] ingredients = inventory.getStorageContents();
			for (ItemStack ingredient : ingredients) {
				if (CustomItem.isCustom(ingredient)) {
					inventory.setResult(new ItemStack(Material.AIR));
					break;
				}
			}
		}

		// Check if there are any custom recipes matching the ingredients
		CustomRecipe[] recipes = CustomItemsPlugin.getInstance().getSet().getRecipes();
		if (recipes.length > 0) {
			// Determine ingredients
			ItemStack[] ingredients = inventory.getStorageContents();
			ingredients = Arrays.copyOfRange(ingredients, 1, ingredients.length);
			Material[] ingredientTypes = new Material[ingredients.length];
			for (int index = 0; index < ingredients.length; index++)
				ingredientTypes[index] = ingredients[index].getType();

			// Shaped recipes first because they have priority
			for (int index = 0; index < recipes.length; index++) {
				if (recipes[index] instanceof ShapedCustomRecipe && recipes[index].shouldAccept(ingredients)) {
					inventory.setResult(recipes[index].getResult());
					shouldInterfere.put(owner.getUniqueId(), true);
					return;
				}
			}

			// No shaped recipe fits, so try the shapeless recipes
			for (int index = 0; index < recipes.length; index++) {
				if (recipes[index] instanceof ShapelessCustomRecipe && recipes[index].shouldAccept(ingredients)) {
					inventory.setResult(recipes[index].getResult());
					shouldInterfere.put(owner.getUniqueId(), true);
					return;
				}
			}
		}
		shouldInterfere.put(owner.getUniqueId(), false);
	}
}